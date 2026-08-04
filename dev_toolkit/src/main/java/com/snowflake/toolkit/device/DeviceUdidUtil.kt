package com.snowflake.toolkit.device

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import com.snowflake.toolkit.manger.ToolKitManager
import com.tencent.mmkv.MMKV
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * 设备号（udid）工具，逻辑对齐公司公共 SDK：
 *
 * ```text
 * getMd5Udid() = MD5(getUserUdidNormal())
 *
 * getUserUdidNormal():
 *   1. 本地 miitUdid
 *   2. 本地 miitOaid（回写为 miitUdid）
 *   3. Settings.Secure.ANDROID_ID（缓存 miitRealAndroidID，并回写 miitUdid）
 *   4. UUID.randomUUID()（回写 miitUdid）
 * ```
 *
 * OAID 通过 Android_CN_OAID 异步获取：多数机型约 **100ms～2s**，个别更慢或失败不回调。
 * 隐私同意后请用 [initOaid] 的超时回调，再去打公共配置接口。
 *
 * @author Melon
 * @date 2026/8/4
 */
object DeviceUdidUtil {

    const val KEY_MIIT_UDID = "miitUdid"
    const val KEY_MIIT_OAID = "miitOaid"
    const val KEY_MIIT_REAL_ANDROID_ID = "miitRealAndroidID"

    const val HEADER_UDID = "udid"

    /** 建议等待 OAID 的默认超时（毫秒） */
    const val DEFAULT_OAID_TIMEOUT_MS = 1_500L

    private const val SP_NAME = "toolkit_miit_device"
    private const val MMKV_ID_SALT = "aexsa2xasae"

    private val INVALID_IDS = setOf(
        "9774d56d682e549c",
        "0000000000000000",
        "0123456789abcdef",
    )

    private val cachedMd5Udid = AtomicReference<String?>(null)
    private val cachedRawUdid = AtomicReference<String?>(null)
    private val oaidInitStarted = AtomicBoolean(false)
    private val mainHandler = Handler(Looper.getMainLooper())

    private val context: Context
        get() = ToolKitManager.instance.getContext()

    private val sp by lazy {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    private val mmkv by lazy {
        MMKV.initialize(context)
        val mmkvId = md5(context.packageName + MMKV_ID_SALT)
        MMKV.mmkvWithID(mmkvId, MMKV.MULTI_PROCESS_MODE)
    }

    /**
     * OAID 初始化结果。超时未拿到 OAID 时 [oaid] 为空，但 [md5Udid] 仍可用（ANDROID_ID/UUID 兜底）。
     */
    data class OaidInitResult(
        val oaid: String?,
        val md5Udid: String,
        val fromCache: Boolean,
        val timedOut: Boolean,
    )

    /**
     * 仅触发异步拉取，不等待。更推荐 [initOaid] 带超时回调的重载。
     */
    @JvmStatic
    fun initOaid() {
        initOaid(DEFAULT_OAID_TIMEOUT_MS, null)
    }

    /**
     * 隐私同意后调用：拉取 OAID，并在「拿到 OAID」或「超时」后回调（主线程，只回调一次）。
     *
     * 典型耗时：已有缓存 ≈ 0；正常 100ms～2s；超时后用 ANDROID_ID/UUID 生成的 udid 继续请求即可。
     *
     * ```kotlin
     * DeviceUdidUtil.initOaid(1500L) { result ->
     *     // 再请求公共配置；头里用 result.md5Udid 或 DeviceUdidUtil.getMd5Udid()
     *     fetchCommonConfig()
     * }
     * ```
     */
    @JvmStatic
    @JvmOverloads
    fun initOaid(
        timeoutMs: Long = DEFAULT_OAID_TIMEOUT_MS,
        callback: ((OaidInitResult) -> Unit)?,
    ) {
        val cached = readId(KEY_MIIT_OAID)
        if (isValidId(cached)) {
            saveOaid(cached!!)
            callback?.let {
                mainHandler.post {
                    it(
                        OaidInitResult(
                            oaid = cached,
                            md5Udid = getMd5Udid(),
                            fromCache = true,
                            timedOut = false,
                        )
                    )
                }
            }
            return
        }

        val finished = AtomicBoolean(false)
        fun complete(oaid: String?, timedOut: Boolean) {
            if (!finished.compareAndSet(false, true)) return
            callback?.invoke(
                OaidInitResult(
                    oaid = readId(KEY_MIIT_OAID),
                    md5Udid = getMd5Udid(),
                    fromCache = false,
                    timedOut = timedOut,
                )
            )
        }

        oaidInitStarted.set(true)
        DeviceOaidHelper.fetch(context) { oaid ->
            // 即便业务侧已超时回调，晚到的 OAID 仍写入，便于后续请求升级
            if (isValidId(oaid)) {
                saveOaid(oaid!!)
            }
            complete(oaid, timedOut = false)
        }

        if (callback != null && timeoutMs > 0) {
            mainHandler.postDelayed({
                complete(oaid = null, timedOut = true)
            }, timeoutMs)
        }
    }

    /**
     * 网络请求公共头设备号：MD5(原始 udid)，32 位小写 hex。
     * 请在隐私同意后使用；本方法不会自动触发 OAID 拉取。
     */
    @JvmStatic
    fun getMd5Udid(): String {
        cachedMd5Udid.get()?.let { return it }
        val value = md5(getUserUdidNormal())
        cachedMd5Udid.compareAndSet(null, value)
        return cachedMd5Udid.get() ?: value
    }

    /**
     * 原始 udid（未 MD5），按公司优先级生成并缓存。
     */
    @JvmStatic
    @Synchronized
    fun getUserUdidNormal(): String {
        cachedRawUdid.get()?.takeIf { isValidId(it) }?.let { return it }

        readId(KEY_MIIT_UDID)?.let { id ->
            cachedRawUdid.set(id)
            return id
        }

        readId(KEY_MIIT_OAID)?.let { id ->
            saveId(KEY_MIIT_UDID, id)
            cachedRawUdid.set(id)
            cachedMd5Udid.set(null)
            return id
        }

        getAndroidIdReal()?.let { id ->
            saveId(KEY_MIIT_REAL_ANDROID_ID, id)
            saveId(KEY_MIIT_UDID, id)
            cachedRawUdid.set(id)
            cachedMd5Udid.set(null)
            return id
        }

        val uuid = UUID.randomUUID().toString()
        saveId(KEY_MIIT_UDID, uuid)
        cachedRawUdid.set(uuid)
        cachedMd5Udid.set(null)
        return uuid
    }

    /**
     * 写入 OAID。默认：无 udid，或当前 udid 等于 ANDROID_ID 时，用 OAID 覆盖 miitUdid。
     */
    @JvmStatic
    @Synchronized
    fun saveOaid(oaid: String, overwriteUdid: Boolean = false) {
        if (!isValidId(oaid)) return
        saveId(KEY_MIIT_OAID, oaid)

        val current = readId(KEY_MIIT_UDID)
        val androidId = readId(KEY_MIIT_REAL_ANDROID_ID)
        val shouldOverwrite = overwriteUdid ||
                !isValidId(current) ||
                (isValidId(androidId) && current == androidId)

        if (shouldOverwrite) {
            saveId(KEY_MIIT_UDID, oaid)
            cachedRawUdid.set(oaid)
            cachedMd5Udid.set(null)
        }
    }

    @JvmStatic
    fun getCachedOaid(): String? = readId(KEY_MIIT_OAID)

    @JvmStatic
    fun getCachedAndroidId(): String? = readId(KEY_MIIT_REAL_ANDROID_ID)

    private fun getAndroidIdReal(): String? {
        return try {
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ).takeIf { isValidId(it) }
        } catch (_: Exception) {
            null
        }
    }

    private fun readId(key: String): String? {
        val fromMmkv = mmkv.decodeString(key, null)
        if (isValidId(fromMmkv)) return fromMmkv

        val fromSp = sp.getString(key, null)
        if (isValidId(fromSp)) {
            mmkv.encode(key, fromSp)
            return fromSp
        }
        return null
    }

    private fun saveId(key: String, value: String) {
        if (!isValidId(value)) return
        sp.edit().putString(key, value).apply()
        mmkv.encode(key, value)
    }

    private fun isValidId(id: String?): Boolean {
        if (id.isNullOrBlank()) return false
        val value = id.trim()
        if (TextUtils.isEmpty(value)) return false
        if (value == "0" || value == "null" || value == "nil") return false
        if (value.matches(Regex("^0+$"))) return false
        if (INVALID_IDS.contains(value.lowercase())) return false
        return true
    }

    private fun md5(input: String): String {
        val digest = MessageDigest.getInstance("MD5").digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
