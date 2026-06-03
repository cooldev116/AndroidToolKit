package com.adwant.kit

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot

class BannerAd(
    private val adId: String,
    private val callback: AdFlowCallback?
) {
    fun show(
        activity: FragmentActivity,
        bannerContainer: ViewGroup,
        width: Int,
        height: Int
    ) {
        AdKitLog.i("showBannerAd called, adId=$adId")
        callback?.onLoadStart(AdType.BANNER, adId)
        val adNativeLoader = TTAdSdk.getAdManager().createAdNative(activity)
        val adSlot = AdSlot.Builder()
            .setCodeId(adId)
            .setImageAcceptedSize(width, height)
            .setExpressViewAcceptedSize(width.toFloat(), height.toFloat())
            .setMediationAdSlot(
                MediationAdSlot.Builder()
                    .setExtraObject("show_adn_load_error_detail", true).build()
            )
            .build()
        adNativeLoader.loadBannerExpressAd(adSlot, object : TTAdNative.NativeExpressAdListener {
            override fun onError(code: Int, msg: String?) {
                AdKitLog.e("loadBannerExpressAd onError, code=$code, msg=$msg, adId=$adId")
                callback?.onLoadFail(AdType.BANNER, adId, code, msg)
            }

            override fun onNativeExpressAdLoad(ads: List<TTNativeExpressAd?>?) {
                if (!ads.isNullOrEmpty() && ads[0] != null) {
                    AdKitLog.i("onNativeExpressAdLoad success, size=${ads.size}, adId=$adId")
                    callback?.onLoadSuccess(AdType.BANNER, adId)
                    realShowBannerAd(ads[0]!!, bannerContainer)
                } else {
                    AdKitLog.w("onNativeExpressAdLoad empty, adId=$adId")
                    callback?.onLoadFail(AdType.BANNER, adId, null, "onNativeExpressAdLoad empty")
                }
            }
        })
    }

    private fun realShowBannerAd(bannerAd: TTNativeExpressAd, bannerContainer: ViewGroup) {
        AdKitLog.i("realShowBannerAd called")
        bannerAd.setExpressInteractionListener(object :
            TTNativeExpressAd.ExpressAdInteractionListener {
            override fun onAdClicked(view: View?, index: Int) {
                AdKitLog.i("onAdClicked, index=$index")
                callback?.onClick(AdType.BANNER, adId)
            }

            override fun onAdShow(view: View?, index: Int) {
                AdKitLog.i("onAdShow, index=$index")
                callback?.onShow(AdType.BANNER, adId)
            }

            override fun onRenderFail(view: View?, msg: String?, code: Int) {
                AdKitLog.e("onRenderFail, code=$code, msg=$msg")
                callback?.onRenderFail(AdType.BANNER, adId, code, msg)
            }

            override fun onRenderSuccess(view: View?, width: Float, height: Float) {
                AdKitLog.i("onRenderSuccess, width=$width, height=$height")
                callback?.onRenderSuccess(AdType.BANNER, adId)
            }
        })
        val bannerView = bannerAd.expressAdView
        bannerContainer.removeAllViews()
        bannerContainer.addView(bannerView)
    }
}
