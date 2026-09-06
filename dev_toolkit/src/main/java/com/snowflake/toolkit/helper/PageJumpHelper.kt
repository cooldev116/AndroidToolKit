package com.snowflake.toolkit.helper

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.snowflake.toolkit.bean.LegalBean
import com.snowflake.toolkit.ext.toast
import com.snowflake.toolkit.net.NetRepository
import com.snowflake.toolkit.net.ToolkitApi
import com.snowflake.toolkit.ui.activity.ToolkitAboutActivity
import com.snowflake.toolkit.ui.activity.ToolkitFeedbackActivity
import com.snowflake.toolkit.ui.activity.ToolkitCommonWebActivity
import com.snowflake.toolkit.utils.AppInfoUtil
import com.snowflake.toolkit.utils.LoadingHandler
import com.snowflake.toolkit.utils.MMKVUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

/**
 * 通用页面跳转门面：隐私政策、用户协议、意见反馈、关于我们等入口。
 *
 * 业务侧只需调用 [openPrivacy] / [openAgreement] / [openFeedback] / [openAbout]；
 * 内部负责拉接口、缓存与打开对应页面。
 * 隐私弹框展示前可调用 [preloadLegal] 预拉，点击时尽量秒开。
 */
object PageJumpHelper {

    private const val TAG = "PageJumpHelper"
    private const val KEY_LEGAL_PREFIX = "toolkit_legal_"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val gson by lazy { Gson() }
    private val fetchMutex = Mutex()

    @Volatile
    private var memoryLegal: LegalBean? = null

    /**
     * 预加载隐私政策 / 用户协议。
     * 适合在首次隐私弹框 show 时调用；失败不影响弹框展示，真正点击时再重试。
     */
    fun preloadLegal() {
        if (memoryLegal != null || readLegalCache() != null) {
            Timber.tag(TAG).d("preloadLegal skip, cache hit")
            return
        }
        scope.launch {
            runCatching { fetchLegal() }
                .onFailure { Timber.tag(TAG).w(it, "preloadLegal failed") }
        }
    }

    /** 打开隐私政策页；无缓存时现场请求，[FragmentActivity] 场景会展示 loading。 */
    fun openPrivacy(context: Context) {
        openLegalPage(context, typeName = "privacy") { bean ->
            bean.privacyTitle to bean.privacyUrl
        }
    }

    /** 打开用户协议页；无缓存时现场请求，[FragmentActivity] 场景会展示 loading。 */
    fun openAgreement(context: Context) {
        openLegalPage(context, typeName = "agreement") { bean ->
            bean.agreementTitle to bean.agreementUrl
        }
    }

    /** 打开原生意见反馈页。 */
    fun openFeedback(context: Context) {
        ToolkitFeedbackActivity.open(context)
    }

    /** 打开关于我们页（图标 / 应用名 / 版本号）。 */
    fun openAbout(context: Context) {
        ToolkitAboutActivity.open(context)
    }

    /**
     * 打开 Web 页。后续客服等若已有 title/url，可直接走此方法，不必再走 legal 接口。
     */
    fun openWeb(context: Context, title: String?, url: String?) {
        if (url.isNullOrBlank()) {
            Timber.tag(TAG).w("openWeb skipped, url empty title=%s", title)
            "页面地址为空".toast()
            return
        }
        ToolkitCommonWebActivity.open(context, title.orEmpty(), url)
    }

    /**
     * 按 legal 数据打开指定页面：优先内存 / MMKV 缓存，未命中再请求。
     */
    private fun openLegalPage(
        context: Context,
        typeName: String,
        picker: (LegalBean) -> Pair<String, String>,
    ) {
        val cached = memoryLegal ?: readLegalCache()?.also { memoryLegal = it }
        if (cached != null) {
            val (title, url) = picker(cached)
            openWeb(context, title, url)
            return
        }

        val activity = context.findFragmentActivity()
        val loadingHandler = if (activity != null) LoadingHandler() else null
        // 持有 Application Context，避免协程回来时 Activity 已销毁仍用原 Context 跳转失败以外的泄漏风险
        val appContext = context.applicationContext

        scope.launch {
            try {
                if (activity != null && !activity.isFinishing) {
                    loadingHandler?.showLoading(activity)
                }
                val legal = fetchLegal()
                val (title, url) = picker(legal)
                val jumpContext = when {
                    activity != null && !activity.isFinishing && !activity.isDestroyed -> activity
                    else -> appContext
                }
                openWeb(jumpContext, title, url)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "openLegalPage failed type=%s", typeName)
                (e.message?.takeIf { it.isNotBlank() } ?: "加载失败，请稍后重试").toast()
            } finally {
                loadingHandler?.dismissLoading()
            }
        }
    }

    /**
     * 请求 legal 并写入内存 + MMKV；[fetchMutex] 保证并发点击 / preload 只打一次网。
     */
    private suspend fun fetchLegal(): LegalBean = fetchMutex.withLock {
        memoryLegal?.let { return it }
        readLegalCache()?.let {
            memoryLegal = it
            return it
        }

        val api = NetRepository.instance.buildApi(ToolkitApi::class.java)
        val response = api.getLegal()
        if (!response.isSuccess()) {
            throw IllegalStateException(response.message?.takeIf { it.isNotBlank() } ?: "获取协议失败")
        }
        val data = response.data
            ?: throw IllegalStateException(response.message?.takeIf { it.isNotBlank() } ?: "协议数据为空")
        memoryLegal = data
        writeLegalCache(data)
        Timber.tag(TAG).d("fetchLegal success package=%s", data.packageName)
        data
    }

    private fun cacheKey(): String = KEY_LEGAL_PREFIX + AppInfoUtil.getPackageName()

    private fun readLegalCache(): LegalBean? {
        val json = MMKVUtil.getString(cacheKey()) ?: return null
        return runCatching { gson.fromJson(json, LegalBean::class.java) }
            .onFailure { Timber.tag(TAG).w(it, "readLegalCache parse failed") }
            .getOrNull()
    }

    private fun writeLegalCache(bean: LegalBean) {
        runCatching {
            MMKVUtil.save(cacheKey(), gson.toJson(bean))
        }.onFailure {
            Timber.tag(TAG).w(it, "writeLegalCache failed")
        }
    }

    private fun Context.findFragmentActivity(): FragmentActivity? {
        var ctx: Context? = this
        while (ctx is android.content.ContextWrapper) {
            if (ctx is FragmentActivity) return ctx
            if (ctx is Activity) return null
            ctx = ctx.baseContext
        }
        return null
    }
}
