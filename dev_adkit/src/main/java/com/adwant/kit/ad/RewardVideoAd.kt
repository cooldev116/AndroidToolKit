package com.adwant.kit.ad

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.adwant.kit.AdFlowCallback
import com.adwant.kit.AdKitLog
import com.adwant.kit.AdType
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTRewardVideoAd
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot
import com.snowflake.toolkit.utils.LoadingHandler

/**
 * 激励视频广告。
 * 请求较慢，加载期间展示 Loading；加载失败、缓存为空、Activity 无效、视频错误或广告真正展示时关闭。
 */
class RewardVideoAd(
    private val adId: String,
    private val callback: AdFlowCallback?
) {
    private val loadingHandler = LoadingHandler()

    fun show(activity: FragmentActivity) {
        AdKitLog.i("showRewardVideo called, adId=$adId")
        loadingHandler.showLoading(activity)
        callback?.onLoadStart(AdType.REWARD, adId)
        val adNativeLoader = TTAdSdk.getAdManager().createAdNative(activity)
        val adSlot = AdSlot.Builder()
            .setCodeId(adId)
            .setOrientation(TTAdConstant.VERTICAL)
            .setMediationAdSlot(
                MediationAdSlot.Builder()
                    .setExtraObject("show_adn_load_error_detail", true).build()
            )
            .build()
        adNativeLoader.loadRewardVideoAd(adSlot, object : TTAdNative.RewardVideoAdListener {
            override fun onError(code: Int, msg: String?) {
                AdKitLog.e("loadRewardVideoAd onError, code=$code, msg=$msg, adId=$adId")
                dismissLoading()
                callback?.onLoadFail(AdType.REWARD, adId, code, msg)
            }

            override fun onRewardVideoAdLoad(ad: TTRewardVideoAd?) {
                AdKitLog.i("onRewardVideoAdLoad, adId=$adId")
                callback?.onLoadSuccess(AdType.REWARD, adId)
            }

            @Deprecated("Deprecated in Java")
            override fun onRewardVideoCached() {
                AdKitLog.i("onRewardVideoCached(deprecated), adId=$adId")
            }

            override fun onRewardVideoCached(ad: TTRewardVideoAd?) {
                AdKitLog.i("onRewardVideoCached, hasAd=${ad != null}, adId=$adId")
                if (ad == null) {
                    dismissLoading()
                    callback?.onLoadFail(AdType.REWARD, adId, null, MSG_REWARD_CACHE_NULL)
                    return
                }
                realShowRewardAd(activity, ad)
            }
        })
    }

    private fun realShowRewardAd(activity: FragmentActivity, rewardAd: TTRewardVideoAd) {
        AdKitLog.i("realShowRewardAd called")
        if (activity.isFinishing || activity.isDestroyed) {
            AdKitLog.w("skip show reward, activity invalid, adId=$adId")
            dismissLoading()
            callback?.onLoadFail(AdType.REWARD, adId, null, MSG_ACTIVITY_INVALID)
            return
        }
        rewardAd.setRewardAdInteractionListener(object :
            TTRewardVideoAd.RewardAdInteractionListener {
            override fun onAdShow() {
                AdKitLog.i("reward onAdShow")
                // 广告已展示，关闭加载框
                dismissLoading()
                callback?.onShow(AdType.REWARD, adId)
            }

            override fun onAdVideoBarClick() {
                AdKitLog.i("reward onAdVideoBarClick")
                callback?.onClick(AdType.REWARD, adId)
            }

            override fun onAdClose() {
                AdKitLog.i("reward onAdClose")
                dismissLoading()
                callback?.onClose(AdType.REWARD, adId)
            }

            override fun onVideoComplete() {
                AdKitLog.i("reward onVideoComplete")
                callback?.onVideoComplete(AdType.REWARD, adId)
            }

            override fun onVideoError() {
                AdKitLog.e("reward onVideoError")
                dismissLoading()
                callback?.onVideoError(AdType.REWARD, adId)
            }

            @Deprecated("Deprecated in Java")
            override fun onRewardVerify(
                verify: Boolean,
                amount: Int,
                name: String?,
                errorCode: Int,
                errorMsg: String?
            ) {
                AdKitLog.i("reward onRewardVerify, verify=$verify, amount=$amount, name=$name, errorCode=$errorCode, errorMsg=$errorMsg")
                callback?.onRewardVerify(adId, verify, amount, name, errorCode, errorMsg)
            }

            @Deprecated("Deprecated in Java")
            override fun onRewardArrived(
                rewardValid: Boolean,
                amount: Int,
                bundle: Bundle?
            ) {
                AdKitLog.i("reward onRewardArrived, rewardValid=$rewardValid, amount=$amount")
                callback?.onRewardArrived(adId, rewardValid, amount, bundle)
            }

            override fun onSkippedVideo() {
                AdKitLog.i("reward onSkippedVideo")
                callback?.onSkipped(AdType.REWARD, adId)
            }
        })
        AdKitLog.i("reward showRewardVideoAd")
        rewardAd.showRewardVideoAd(activity)
    }

    private fun dismissLoading() {
        loadingHandler.dismissLoading()
    }

    companion object {
        private const val MSG_REWARD_CACHE_NULL = "激励视频缓存为空"
        private const val MSG_ACTIVITY_INVALID = "页面已关闭，无法展示激励视频"
    }
}
