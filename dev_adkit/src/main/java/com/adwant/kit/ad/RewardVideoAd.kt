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

class RewardVideoAd(
    private val adId: String,
    private val callback: AdFlowCallback?
) {
    fun show(activity: FragmentActivity) {
        AdKitLog.i("showRewardVideo called, adId=$adId")
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
                ad?.let {
                    realShowRewardAd(activity, it)
                } ?: AdKitLog.w("onRewardVideoCached but ad is null, adId=$adId")
            }
        })
    }

    private fun realShowRewardAd(activity: FragmentActivity, rewardAd: TTRewardVideoAd) {
        AdKitLog.i("realShowRewardAd called")
        rewardAd.setRewardAdInteractionListener(object :
            TTRewardVideoAd.RewardAdInteractionListener {
            override fun onAdShow() {
                AdKitLog.i("reward onAdShow")
                callback?.onShow(AdType.REWARD, adId)
            }

            override fun onAdVideoBarClick() {
                AdKitLog.i("reward onAdVideoBarClick")
                callback?.onClick(AdType.REWARD, adId)
            }

            override fun onAdClose() {
                AdKitLog.i("reward onAdClose")
                callback?.onClose(AdType.REWARD, adId)
            }

            override fun onVideoComplete() {
                AdKitLog.i("reward onVideoComplete")
                callback?.onVideoComplete(AdType.REWARD, adId)
            }

            override fun onVideoError() {
                AdKitLog.e("reward onVideoError")
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
}