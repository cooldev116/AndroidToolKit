package com.adwant.kit

import androidx.fragment.app.FragmentActivity
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot

class InterstitialAd(
    private val adId: String,
    private val callback: AdFlowCallback?
) {
    fun show(activity: FragmentActivity) {
        AdKitLog.i("showInterstitialAd called, adId=$adId")
        callback?.onLoadStart(AdType.INTERSTITIAL, adId)
        val adNativeLoader = TTAdSdk.getAdManager().createAdNative(activity)
        val adSlot = AdSlot.Builder()
            .setCodeId(adId)
            .setOrientation(TTAdConstant.ORIENTATION_VERTICAL)
            .setMediationAdSlot(
                MediationAdSlot.Builder().setMuted(true).setVolume(0.7f).setBidNotify(true)
                    .setExtraObject("show_adn_load_error_detail", true).build()
            )
            .build()
        adNativeLoader.loadFullScreenVideoAd(adSlot, object : TTAdNative.FullScreenVideoAdListener {
            override fun onError(code: Int, msg: String?) {
                AdKitLog.e("loadFullScreenVideoAd onError, code=$code, msg=$msg, adId=$adId")
                callback?.onLoadFail(AdType.INTERSTITIAL, adId, code, msg)
            }

            override fun onFullScreenVideoAdLoad(ad: TTFullScreenVideoAd?) {
                AdKitLog.i("onFullScreenVideoAdLoad, adId=$adId")
                callback?.onLoadSuccess(AdType.INTERSTITIAL, adId)
            }

            @Deprecated("Deprecated in Java")
            override fun onFullScreenVideoCached() {
                AdKitLog.i("onFullScreenVideoCached(deprecated), adId=$adId")
            }

            override fun onFullScreenVideoCached(ad: TTFullScreenVideoAd?) {
                AdKitLog.i("onFullScreenVideoCached, ready=${ad?.mediationManager?.isReady}, adId=$adId")
                if (ad?.mediationManager?.isReady == true) {
                    realShowInterstitialAd(activity, ad)
                }
            }
        })
    }

    private fun realShowInterstitialAd(activity: FragmentActivity, ad: TTFullScreenVideoAd?) {
        AdKitLog.i("realShowInterstitialAd called")
        ad?.setFullScreenVideoAdInteractionListener(object :
            TTFullScreenVideoAd.FullScreenVideoAdInteractionListener {
            override fun onAdShow() {
                AdKitLog.i("onAdShow")
                callback?.onShow(AdType.INTERSTITIAL, adId)
                AdKit.instance.setShowInterstitialCount()
            }

            override fun onAdVideoBarClick() {
                AdKitLog.i("onAdVideoBarClick")
                callback?.onClick(AdType.INTERSTITIAL, adId)
            }

            override fun onAdClose() {
                AdKitLog.i("onAdClose")
                ad.mediationManager?.destroy()
                callback?.onClose(AdType.INTERSTITIAL, adId)
            }

            override fun onVideoComplete() {
                AdKitLog.i("onVideoComplete")
                callback?.onVideoComplete(AdType.INTERSTITIAL, adId)
            }

            override fun onSkippedVideo() {
                AdKitLog.i("onSkippedVideo")
                callback?.onSkipped(AdType.INTERSTITIAL, adId)
            }
        })
        ad?.showFullScreenVideoAd(activity)
    }
}
