package com.adwant.kit.ad

import android.R
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.adwant.kit.AdFlowCallback
import com.adwant.kit.AdKitLog
import com.adwant.kit.AdType
import com.adwant.kit.utils.ScreenUtils
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.CSJAdError
import com.bytedance.sdk.openadsdk.CSJSplashAd
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdSdk

/**
 * 开屏广告：支持「加载即展示」与「预加载后延后展示」。
 *
 * 双开屏场景下，第一开屏 onLoad 后 [preload]，第一开屏 onClose 后 [showPreloaded]，
 * 以避免第二开屏在第一开屏关闭后才开始请求。
 */
class SplashAd(
    private val adId: String,
    private val callback: AdFlowCallback?
) {
    private enum class LoadState {
        IDLE,
        LOADING,
        SUCCESS,
        FAIL
    }

    private var loadState = LoadState.IDLE
    private var cachedAd: CSJSplashAd? = null
    /** 加载成功后是否立即展示（[show] 为 true，[preload] 为 false） */
    private var autoShowOnLoad = true
    /** 第一开屏已关闭、等待第二开屏加载完成后自动展示时持有的 Activity */
    private var pendingShowActivity: FragmentActivity? = null
    private var hasShown = false

    /**
     * 加载并在成功后立即展示（单开屏默认路径）。
     */
    fun show(activity: FragmentActivity) {
        autoShowOnLoad = true
        loadInternal(activity)
    }

    /**
     * 仅预加载，成功后缓存素材，不展示；需再调 [showPreloaded]。
     */
    fun preload(activity: FragmentActivity) {
        autoShowOnLoad = false
        loadInternal(activity)
    }

    /**
     * 展示已预加载的开屏。
     * - 已加载成功：立即展示
     * - 仍在加载：加载成功后自动展示
     * - 已失败 / 未开始加载：不再重复回调（失败已在加载阶段通知）
     */
    fun showPreloaded(activity: FragmentActivity) {
        if (hasShown) return
        when (loadState) {
            LoadState.SUCCESS -> realShowSplashAd(activity, cachedAd)
            LoadState.LOADING -> {
                // 第一开屏已关，第二开屏尚未就绪：就绪后自动展示
                pendingShowActivity = activity
                autoShowOnLoad = true
            }
            LoadState.IDLE -> {
                // 兜底：未预加载时退化为加载并展示
                autoShowOnLoad = true
                loadInternal(activity)
            }
            LoadState.FAIL -> {
                AdKitLog.i("showPreloaded skipped, already failed, adId=$adId")
            }
        }
    }

    /**
     * 是否已加载失败（供双开屏在第一开屏关闭时决定是否跳过第二开屏）。
     */
    fun isLoadFailed(): Boolean = loadState == LoadState.FAIL

    /**
     * 释放缓存广告，避免 Activity 销毁后仍持有穿山甲对象。
     */
    fun destroy() {
        pendingShowActivity = null
        if (!hasShown) {
            cachedAd?.mediationManager?.destroy()
        }
        cachedAd = null
    }

    private fun loadInternal(activity: FragmentActivity) {
        if (loadState == LoadState.LOADING || loadState == LoadState.SUCCESS) {
            AdKitLog.i("loadSplashAd skipped, state=$loadState, adId=$adId")
            return
        }
        loadState = LoadState.LOADING
        AdKitLog.i("loadSplashAd called, autoShow=$autoShowOnLoad, adId=$adId")
        callback?.onLoadStart(AdType.SPLASH, adId)
        val adNativeLoader = TTAdSdk.getAdManager().createAdNative(activity)
        val screenWidth = ScreenUtils.getScreenWidth(activity)
        val screenHeight = ScreenUtils.getScreenHeight(activity)
        val adSlot = AdSlot.Builder()
            .setCodeId(adId)
            .setImageAcceptedSize(screenWidth, screenHeight)
            .setExpressViewAcceptedSize(screenWidth.toFloat(), screenHeight.toFloat())
            .build()
        adNativeLoader.loadSplashAd(adSlot, object : TTAdNative.CSJSplashAdListener {
            override fun onSplashLoadSuccess(ad: CSJSplashAd?) {
                AdKitLog.i("onSplashLoadSuccess------>$adId")
                loadState = LoadState.SUCCESS
                cachedAd = ad
                callback?.onLoadSuccess(AdType.SPLASH, adId)
                val showTarget = pendingShowActivity ?: activity.takeIf { autoShowOnLoad }
                pendingShowActivity = null
                if (showTarget != null && isActivityValid(showTarget)) {
                    realShowSplashAd(showTarget, ad)
                }
            }

            override fun onSplashLoadFail(error: CSJAdError?) {
                AdKitLog.i("onSplashLoadFail------->${error?.code}----->${error?.msg}------>$adId")
                loadState = LoadState.FAIL
                pendingShowActivity = null
                callback?.onLoadFail(AdType.SPLASH, adId, error?.code, error?.msg)
            }

            override fun onSplashRenderSuccess(ad: CSJSplashAd?) {
                AdKitLog.i("onSplashRenderSuccess----->$adId")
                callback?.onRenderSuccess(AdType.SPLASH, adId)
            }

            override fun onSplashRenderFail(ad: CSJSplashAd?, error: CSJAdError?) {
                AdKitLog.i("onSplashRenderFail----->$adId")
                callback?.onRenderFail(AdType.SPLASH, adId, error?.code, error?.msg)
            }
        }, 3500)
    }

    /**
     * 真正展示广告
     */
    private fun realShowSplashAd(activity: FragmentActivity, splashAd: CSJSplashAd?) {
        if (hasShown) return
        if (!isActivityValid(activity)) {
            AdKitLog.i("realShowSplashAd aborted, activity invalid, adId=$adId")
            return
        }
        hasShown = true
        val container = generateContainerView(activity)
        splashAd?.setSplashAdListener(object : CSJSplashAd.SplashAdListener {
            override fun onSplashAdShow(ad: CSJSplashAd?) {
                AdKitLog.i("onSplashAdShow----->$adId")
                callback?.onShow(AdType.SPLASH, adId)
            }

            override fun onSplashAdClick(ad: CSJSplashAd?) {
                AdKitLog.i("onSplashAdClick----->$adId")
                callback?.onClick(AdType.SPLASH, adId)
            }

            override fun onSplashAdClose(ad: CSJSplashAd?, closeType: Int) {
                AdKitLog.i("onSplashAdClose----->$adId")
                ad?.mediationManager?.destroy()
                cachedAd = null
                removeSplashContainer(container)
                callback?.onClose(AdType.SPLASH, adId)
            }
        })
        splashAd?.showSplashView(container)
    }

    private fun isActivityValid(activity: FragmentActivity): Boolean {
        return !activity.isFinishing && !activity.isDestroyed
    }

    /**
     * 移除广告的容器
     */
    private fun removeSplashContainer(container: FrameLayout) {
        val parent = container.parent as? ViewGroup ?: return
        parent.removeView(container)
    }

    private fun generateContainerView(activity: FragmentActivity): FrameLayout {
        val container = FrameLayout(activity).apply {
            isClickable = true
            bringToFront()
        }
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        val rootView = activity.findViewById<FrameLayout>(R.id.content)
        rootView.addView(container, params)
        return container
    }
}
