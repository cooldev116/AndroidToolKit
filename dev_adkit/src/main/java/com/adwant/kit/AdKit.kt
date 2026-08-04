package com.adwant.kit

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
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

    companion object {
        val instance by lazy {
            AdKit()
        }
    }

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
        if (!isAllowShowAd) return
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
        if (!isAllowShowAd) return
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
        if (!isAllowShowAd) return
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
        if (!isAllowShowAd) return
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
        if (!isAllowShowAd) return
        RewardVideoAd(adId, callback).show(activity)
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
     * 获取已经展示插屏次数
     */
    fun getShowInterstitialCount() = showedInterstitialCount
}
