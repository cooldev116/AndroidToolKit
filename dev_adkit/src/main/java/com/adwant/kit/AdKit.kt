package com.adwant.kit

import android.app.Application
import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.adwant.kit.ad.BannerAd
import com.adwant.kit.ad.InterstitialAd
import com.adwant.kit.ad.NativeAd
import com.adwant.kit.ad.RewardVideoAd
import com.adwant.kit.ad.SplashAd
import com.adwant.kit.ui.SplashBackendAdActivity
import com.adwant.kit.utils.ScreenUtils
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.bytedance.sdk.openadsdk.TTAdSdk

/**
 * @description:
 * @author:Melon
 * @date:2026/3/15
 */
class AdKit private constructor() {

    /**
     * 是否是debug模式
     */
    private var isDebug = false

    /**
     * 是否允许展示广告
     */
    private var isAllowShowAd = true

    /**
     * 已经展示插屏次数
     */
    private var showedInterstitialCount = 0

    /**
     * 是否初始化
     */
    private var isInit = false

    /**
     * 后台开屏监听（只启用一次）
     */
    private var splashBackendWatcher: SplashBackendWatcher? = null

    /**
     * 初始化
     */
    fun init(
        context: Context,
        appId: String,
        customController: DefaultCustomController = DefaultCustomController(),
        isDebug: Boolean = false,
        callback: ((Boolean, String) -> Unit)? = null
    ) {
        if (isInit) return
        this.isDebug = isDebug
        AdKitLog.i("init called, isDebug=$isDebug")
        TTAdSdk.init(context, buildTTAdConfig(appId, customController))
        TTAdSdk.start(object : TTAdSdk.Callback {
            override fun success() {
                AdKitLog.i("TTAdSdk start success")
                callback?.invoke(true, "初始化成功")
            }

            override fun fail(code: Int, msg: String?) {
                AdKitLog.e("TTAdSdk start failed: $code, msg=$msg")
                callback?.invoke(false, "$code:$msg")
            }
        })
    }

    /**
     * 启用后台开屏：退出后台达到 [thresholdMs] 后再回前台时，启动 [splashActivityClass]。
     * 一般由 [com.adwant.kit.ui.SplashStartAdActivity] 在 SDK 初始化成功后自动调用；重复调用无效。
     */
    fun enableBackendSplash(
        application: Application,
        splashActivityClass: Class<out SplashBackendAdActivity>,
        thresholdMs: Long = AdConfig.BACKEND_SPLASH_THRESHOLD_MS
    ) {
        if (splashBackendWatcher != null) {
            AdKitLog.i("enableBackendSplash ignored, already enabled")
            return
        }
        splashBackendWatcher = SplashBackendWatcher(
            application = application,
            splashActivityClass = splashActivityClass,
            thresholdMs = thresholdMs
        ).also { it.start() }
    }

    /**
     * 获取是否debug模式
     */
    fun getDebug(): Boolean = isDebug

    /**
     * 设置是否debug模式，debug模式有日志输出
     */
    fun setDebug(isDebug: Boolean) {
        this.isDebug = isDebug
        AdKitLog.i("setDebug changed to $isDebug")
    }

    /**
     * 是否展示广告
     */
    fun setAllowShowAd(isShow: Boolean) {
        isAllowShowAd = isShow
    }

    /**
     * 获取是否允许展示广告
     */
    fun getIsAllowShowAd() = isAllowShowAd

    /**
     * 广告配置
     */
    private fun buildTTAdConfig(
        appId: String,
        customController: DefaultCustomController
    ): TTAdConfig {
        return TTAdConfig.Builder().appId(appId)
            .customController(customController)
            .useMediation(true)
            .build()
    }

    /**
     * 展示闪屏广告
     */
    fun showSplashAd(
        activity: FragmentActivity,
        adId: String,
        callback: AdFlowCallback? = null
    ) {
        if (!checkAllowShowAd(AdType.SPLASH, adId, callback)) return
        SplashAd(adId, callback).show(activity)
    }

    /**
     * 展示插屏广告
     */
    fun showInterstitialAd(
        activity: FragmentActivity,
        adId: String,
        callback: AdFlowCallback? = null
    ) {
        if (!checkAllowShowAd(AdType.INTERSTITIAL, adId, callback)) return
        if (!checkInterstitialMaxCount(adId, callback)) return
        InterstitialAd(adId, callback).show(activity)
    }

    /**
     * 展示banner广告
     */
    fun showBannerAd(
        activity: FragmentActivity,
        adId: String,
        bannerContainer: ViewGroup,
        width: Int = ScreenUtils.getScreenWidth(activity),
        height: Int = ScreenUtils.dpToPx(activity, 50f),
        callback: AdFlowCallback? = null
    ) {
        if (!checkAllowShowAd(AdType.BANNER, adId, callback)) return
        BannerAd(adId, callback).show(activity, bannerContainer, width, height)
    }

    /**
     * 展示信息流广告
     */
    fun showNativeAd(
        activity: FragmentActivity,
        adId: String,
        nativeContainer: ViewGroup,
        width: Int = ScreenUtils.getScreenWidth(activity),
        height: Int,
        callback: AdFlowCallback? = null
    ) {
        if (!checkAllowShowAd(AdType.NATIVE, adId, callback)) return
        NativeAd(adId, callback).show(activity, nativeContainer, width, height)
    }

    /**
     * 展示激励视频
     */
    fun showRewardVideo(
        activity: FragmentActivity,
        adId: String,
        callback: AdFlowCallback? = null
    ) {
        if (!checkAllowShowAd(AdType.REWARD, adId, callback)) return
        RewardVideoAd(adId, callback).show(activity)
    }

    /**
     * 不允许展示时走失败回调，上层扩展可统一映射到 onClose。
     */
    private fun checkAllowShowAd(
        type: AdType,
        adId: String,
        callback: AdFlowCallback?
    ): Boolean {
        if (isAllowShowAd) return true
        AdKitLog.i("isAllowShowAd=false, skip show, type=$type, adId=$adId")
        callback?.onLoadFail(type, adId, null, MSG_NOT_ALLOW_SHOW_AD)
        return false
    }

    /**
     * 插屏达到最大展示次数时不再请求/展示。
     */
    private fun checkInterstitialMaxCount(
        adId: String,
        callback: AdFlowCallback?
    ): Boolean {
        if (showedInterstitialCount < AdConfig.DEFAULT_MAX_INTERSTITIAL) return true
        AdKitLog.i(
            "interstitial max reached: $showedInterstitialCount/${AdConfig.DEFAULT_MAX_INTERSTITIAL}, skip show, adId=$adId"
        )
        callback?.onLoadFail(AdType.INTERSTITIAL, adId, null, MSG_INTERSTITIAL_MAX_REACHED)
        return false
    }

    /**
     * 设置插屏已经展示次数
     */
    fun setShowInterstitialCount(count: Int? = null) {
        count?.let {
            showedInterstitialCount += it
        } ?: ++showedInterstitialCount
    }

    /**
     * 重置插屏已展示次数，使本进程内可重新累计至上限。
     * 一般在应用退到后台时由 [SplashBackendWatcher] 自动调用。
     */
    fun resetShowInterstitialCount() {
        if (showedInterstitialCount == 0) return
        AdKitLog.i("resetShowInterstitialCount: $showedInterstitialCount -> 0")
        showedInterstitialCount = 0
    }

    /**
     * 获取已经展示插屏次数
     */
    fun getShowInterstitialCount() = showedInterstitialCount

    companion object {
        const val MSG_NOT_ALLOW_SHOW_AD = "不允许展示广告"
        const val MSG_INTERSTITIAL_MAX_REACHED = "插屏已达最大展示次数"

        val instance by lazy {
            AdKit()
        }
    }
}
