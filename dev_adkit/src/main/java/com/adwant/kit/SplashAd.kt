package com.adwant.kit

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.adwant.kit.utils.ScreenUtils
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.CSJAdError
import com.bytedance.sdk.openadsdk.CSJSplashAd
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdSdk

class SplashAd(
    private val adId: String,
    private val callback: AdFlowCallback?
) {
    fun show(activity: FragmentActivity) {
        AdKitLog.i("showSplashAd called, adId=$adId")
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
                callback?.onLoadSuccess(AdType.SPLASH, adId)
                realShowSplashAd(activity, ad)
            }

            override fun onSplashLoadFail(error: CSJAdError?) {
                AdKitLog.i("onSplashLoadFail------->${error?.code}----->${error?.msg}------>$adId")
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
                removeSplashContainer(container)
                callback?.onClose(AdType.SPLASH, adId)
            }
        })
        splashAd?.showSplashView(container)
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
        val rootView = activity.findViewById<FrameLayout>(android.R.id.content)
        rootView.addView(container, params)
        return container
    }
}
