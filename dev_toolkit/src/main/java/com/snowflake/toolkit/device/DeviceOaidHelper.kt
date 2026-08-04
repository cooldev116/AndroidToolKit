package com.snowflake.toolkit.device

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.bun.miitmdid.core.InfoCode
import com.bun.miitmdid.core.MdidSdkHelper
import com.bun.miitmdid.interfaces.IIdentifierListener
import com.github.gzuliyujiang.oaid.DeviceID
import com.github.gzuliyujiang.oaid.IGetter
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 对齐公司公共 SDK：MSA（MdidSdkHelper）+ Android_CN_OAID（DeviceID.getOAID）双通道拉 OAID。
 *
 * 耗时不固定：多数机型约 100ms～2s；MSA 可能 `INIT_INFO_RESULT_DELAY` 更晚；失败时可能永不回调。
 * 业务侧请用 [DeviceUdidUtil.initOaid] 的超时回调，不要假定立刻完成。
 */
internal object DeviceOaidHelper {

    private const val TAG = "DeviceOaidHelper"
    private const val ASSET_OAID_CERT = "oaid.cert.pem"

    private val started = AtomicBoolean(false)
    private val finished = AtomicBoolean(false)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val waiters = CopyOnWriteArrayList<(String?) -> Unit>()

    @Volatile
    private var msaCertContent: String? = null

    @Volatile
    private var resultOaid: String? = null

    fun setMsaCert(certContent: String?) {
        msaCertContent = certContent?.takeIf { it.isNotBlank() }
    }

    /**
     * 拉取 OAID。完成后主线程回调 [onFinished]（成功传 oaid，失败/未拿到传 null）。
     * 已完成后的再次调用会立即回调。
     */
    fun fetch(context: Context, onFinished: (String?) -> Unit) {
        if (finished.get()) {
            mainHandler.post { onFinished(resultOaid) }
            return
        }
        waiters.add(onFinished)
        if (!started.compareAndSet(false, true)) {
            return
        }

        val appContext = context.applicationContext
        fetchFromCnOaid(appContext)
        fetchFromMsa(appContext)
    }

    fun notifySuccess(oaid: String) {
        if (oaid.isBlank()) return
        if (!finished.compareAndSet(false, true)) return
        resultOaid = oaid
        dispatch(oaid)
    }

    private fun dispatch(oaid: String?) {
        val list = waiters.toList()
        waiters.clear()
        mainHandler.post {
            list.forEach { it.invoke(oaid) }
        }
    }

    private fun fetchFromCnOaid(context: Context) {
        try {
            DeviceID.getOAID(context, object : IGetter {
                override fun onOAIDGetComplete(result: String?) {
                    if (!result.isNullOrBlank()) {
                        Log.d(TAG, "CN_OAID success")
                        notifySuccess(result)
                    }
                }

                override fun onOAIDGetError(error: Exception?) {
                    Log.d(TAG, "CN_OAID error: ${error?.message}")
                }
            })
        } catch (t: Throwable) {
            Log.e(TAG, "CN_OAID failed", t)
        }
    }

    private fun fetchFromMsa(context: Context) {
        try {
            val cert = resolveMsaCert(context)
            if (!cert.isNullOrBlank()) {
                val ok = MdidSdkHelper.InitCert(context, cert)
                Log.d(TAG, "MSA InitCert=$ok")
                if (!ok) return
            } else {
                Log.d(TAG, "MSA cert missing, skip InitSdk（仍依赖 CN_OAID）")
                return
            }

            val code = MdidSdkHelper.InitSdk(context, true, IIdentifierListener { supplier ->
                try {
                    if (supplier != null && supplier.isSupported) {
                        val oaid = supplier.oaid
                        if (!oaid.isNullOrBlank()) {
                            Log.d(TAG, "MSA OAID success")
                            notifySuccess(oaid)
                        }
                    } else {
                        Log.d(TAG, "MSA not supported / limited=${supplier?.isLimited}")
                    }
                } catch (t: Throwable) {
                    Log.e(TAG, "MSA onSupport error", t)
                }
            })
            logMsaInitCode(code)
        } catch (t: Throwable) {
            Log.e(TAG, "MSA failed", t)
        }
    }

    private fun resolveMsaCert(context: Context): String? {
        msaCertContent?.let { return it }
        val pkgPem = "${context.packageName}.cert.pem"
        loadPemFromAssets(context, pkgPem)?.let { return it }
        return loadPemFromAssets(context, ASSET_OAID_CERT)
    }

    private fun loadPemFromAssets(context: Context, assetName: String): String? {
        return try {
            context.assets.open(assetName).bufferedReader().use { it.readText() }
                .takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }

    private fun logMsaInitCode(code: Int) {
        val msg = when (code) {
            InfoCode.INIT_INFO_RESULT_OK -> "INIT_INFO_RESULT_OK"
            InfoCode.INIT_INFO_RESULT_DELAY -> "INIT_INFO_RESULT_DELAY"
            InfoCode.INIT_ERROR_CERT_ERROR -> "INIT_ERROR_CERT_ERROR"
            InfoCode.INIT_ERROR_DEVICE_NOSUPPORT -> "INIT_ERROR_DEVICE_NOSUPPORT"
            InfoCode.INIT_ERROR_LOAD_CONFIGFILE -> "INIT_ERROR_LOAD_CONFIGFILE"
            InfoCode.INIT_ERROR_MANUFACTURER_NOSUPPORT -> "INIT_ERROR_MANUFACTURER_NOSUPPORT"
            InfoCode.INIT_ERROR_SDK_CALL_ERROR -> "INIT_ERROR_SDK_CALL_ERROR"
            else -> "unknown($code)"
        }
        Log.d(TAG, "MSA InitSdk code=$msg")
    }
}
