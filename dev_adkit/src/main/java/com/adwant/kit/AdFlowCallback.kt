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
 * 无默认实现，业务侧请继承 [AbsAdFlowCallback]。
 */
 interface AdFlowCallback {
    fun onLoadStart(type: AdType, adId: String)
    fun onLoadSuccess(type: AdType, adId: String)
    fun onLoadFail(type: AdType, adId: String, code: Int? = null, msg: String? = null)
    fun onRenderSuccess(type: AdType, adId: String)
    fun onRenderFail(type: AdType, adId: String, code: Int? = null, msg: String? = null)
    fun onShow(type: AdType, adId: String)
    fun onClick(type: AdType, adId: String)
    fun onClose(type: AdType, adId: String)
    fun onVideoComplete(type: AdType, adId: String)
    fun onVideoError(type: AdType, adId: String)
    fun onSkipped(type: AdType, adId: String)
    fun onRewardVerify(
        adId: String,
        verify: Boolean,
        amount: Int,
        name: String?,
        errorCode: Int,
        errorMsg: String?
    )

    fun onRewardArrived(adId: String, rewardValid: Boolean, amount: Int, bundle: Bundle?)
}

/**
 * [AdFlowCallback] 中间抽象层：非关键回调给空实现，失败与关闭回调保持抽象。
 */
abstract class AbsAdFlowCallback : AdFlowCallback {
    override fun onLoadStart(type: AdType, adId: String) {}
    override fun onLoadSuccess(type: AdType, adId: String) {}
    override fun onRenderSuccess(type: AdType, adId: String) {}
    override fun onShow(type: AdType, adId: String) {}
    override fun onClick(type: AdType, adId: String) {}
    override fun onVideoComplete(type: AdType, adId: String) {}
    override fun onSkipped(type: AdType, adId: String) {}
    override fun onRewardVerify(
        adId: String,
        verify: Boolean,
        amount: Int,
        name: String?,
        errorCode: Int,
        errorMsg: String?
    ) {
    }

    override fun onRewardArrived(
        adId: String,
        rewardValid: Boolean,
        amount: Int,
        bundle: Bundle?
    ) {
    }

    abstract override fun onLoadFail(
        type: AdType,
        adId: String,
        code: Int?,
        msg: String?
    )

    abstract override fun onRenderFail(
        type: AdType,
        adId: String,
        code: Int?,
        msg: String?
    )

    abstract override fun onVideoError(type: AdType, adId: String)

    abstract override fun onClose(type: AdType, adId: String)
}
