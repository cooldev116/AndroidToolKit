package com.adwant.kit

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTFeedAd
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot
import com.bytedance.sdk.openadsdk.mediation.ad.MediationExpressRenderListener

class NativeAd(
    private val adId: String,
    private val callback: AdFlowCallback?
) {
    fun show(activity: FragmentActivity, nativeContainer: ViewGroup, width: Int, height: Int) {
        AdKitLog.i("showNativeAd called, adId=$adId")
        callback?.onLoadStart(AdType.NATIVE, adId)
        val adNativeLoader = TTAdSdk.getAdManager().createAdNative(activity)
        val adSlot = AdSlot.Builder()
            .setCodeId(adId)
            .setImageAcceptedSize(width, height)
            .setAdCount(1)
            .setMediationAdSlot(
                MediationAdSlot.Builder()
                    .setExtraObject("show_adn_load_error_detail", true).build()
            )
            .build()
        adNativeLoader.loadFeedAd(adSlot, object : TTAdNative.FeedAdListener {
            override fun onError(code: Int, msg: String?) {
                AdKitLog.e("loadFeedAd onError, code=$code, msg=$msg, adId=$adId")
                callback?.onLoadFail(AdType.NATIVE, adId, code, msg)
            }

            override fun onFeedAdLoad(ads: List<TTFeedAd?>?) {
                if (!ads.isNullOrEmpty() && ads[0] != null) {
                    AdKitLog.i("onFeedAdLoad success, size=${ads.size}, adId=$adId")
                    callback?.onLoadSuccess(AdType.NATIVE, adId)
                    realShowNativeAd(ads[0]!!, nativeContainer)
                } else {
                    AdKitLog.w("onFeedAdLoad empty, adId=$adId")
                    callback?.onLoadFail(AdType.NATIVE, adId, null, "onFeedAdLoad empty")
                }
            }
        })
    }

    private fun realShowNativeAd(feedAd: TTFeedAd, nativeContainer: ViewGroup) {
        AdKitLog.i("showRealNativeAd called")
        feedAd.setExpressRenderListener(object : MediationExpressRenderListener {
            override fun onRenderFail(view: View?, msg: String?, code: Int) {
                AdKitLog.e("onRenderFail, code=$code, msg=$msg")
                callback?.onRenderFail(AdType.NATIVE, adId, code, msg)
            }

            override fun onAdClick() {
                AdKitLog.i("onAdClick")
                callback?.onClick(AdType.NATIVE, adId)
            }

            override fun onAdShow() {
                AdKitLog.i("onAdShow")
                callback?.onShow(AdType.NATIVE, adId)
            }

            override fun onRenderSuccess(
                view: View?,
                width: Float,
                height: Float,
                isExpress: Boolean
            ) {
                AdKitLog.i("onRenderSuccess, width=$width, height=$height, isExpress=$isExpress")
                callback?.onRenderSuccess(AdType.NATIVE, adId)
                val nativeView = feedAd.adView
                nativeContainer.removeAllViews()
                nativeContainer.addView(nativeView)
            }
        })
        AdKitLog.i("native ad render()")
        feedAd.render()
    }
}
