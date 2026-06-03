package com.adwant.kit

import android.os.Bundle

enum class AdType {
    SPLASH,
    INTERSTITIAL,
    BANNER,
    NATIVE,
    REWARD
}

/**
 * Generic callback for full ad lifecycle.
 */
interface AdFlowCallback {
    fun onLoadStart(type: AdType, adId: String) {}
    fun onLoadSuccess(type: AdType, adId: String) {}
    fun onLoadFail(type: AdType, adId: String, code: Int? = null, msg: String? = null) {}
    fun onRenderSuccess(type: AdType, adId: String) {}
    fun onRenderFail(type: AdType, adId: String, code: Int? = null, msg: String? = null) {}
    fun onShow(type: AdType, adId: String) {}
    fun onClick(type: AdType, adId: String) {}
    fun onClose(type: AdType, adId: String) {}
    fun onVideoComplete(type: AdType, adId: String) {}
    fun onVideoError(type: AdType, adId: String) {}
    fun onSkipped(type: AdType, adId: String) {}
    fun onRewardVerify(adId: String, verify: Boolean, amount: Int, name: String?, errorCode: Int, errorMsg: String?) {}
    fun onRewardArrived(adId: String, rewardValid: Boolean, amount: Int, bundle: Bundle?) {}
}
