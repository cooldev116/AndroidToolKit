package com.adwant.kit.ext

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.adwant.kit.AbsAdFlowCallback
import com.adwant.kit.AdFlowCallback
import com.adwant.kit.AdKit
import com.adwant.kit.AdKitLog
import com.adwant.kit.AdType
import com.adwant.kit.ui.SplashBackendAdActivity
import com.adwant.kit.utils.ScreenUtils

/**
 * 广告展示扩展方法
 *
 * [onClose] 统一出口：
 * - isAllowShowAd=false → (true, msg)，视为可继续业务流程
 * - 请求/渲染失败 → (false, msg)
 * - 广告真正关闭 → (false, null)；激励视频关闭 → (verify, null)
 */

/**
 * 闪屏广告
 */
fun FragmentActivity.showSplashAd(
    adId: String,
    callback: AdFlowCallback? = null,
    onClose: ((Boolean, String?) -> Unit)? = null
) {
    AdKit.instance.showSplashAd(this, adId, wrapAdCloseCallback(callback, onClose))
}

/**
 * 闪屏广告
 */
fun Fragment.showSplashAd(
    adId: String,
    callback: AdFlowCallback? = null,
    onClose: ((Boolean, String?) -> Unit)? = null
) {
    AdKit.instance.showSplashAd(
        requireActivity(),
        adId,
        wrapAdCloseCallback(callback, onClose)
    )
}

/**
 * Activity展示插屏广告扩展
 */
fun FragmentActivity.showInterstitialAd(
    adId: String,
    callback: AdFlowCallback? = null,
    onClose: ((Boolean, String?) -> Unit)? = null
) {
    AdKit.instance.showInterstitialAd(this, adId, wrapAdCloseCallback(callback, onClose))
}

/**
 * Fragment展示插屏广告扩展
 */
fun Fragment.showInterstitialAd(
    adId: String,
    callback: AdFlowCallback? = null,
    onClose: ((Boolean, String?) -> Unit)? = null
) {
    AdKit.instance.showInterstitialAd(
        requireActivity(),
        adId,
        wrapAdCloseCallback(callback, onClose)
    )
}

/**
 * Activity展示Banner广告扩展
 */
fun FragmentActivity.showBannerAd(
    adId: String,
    container: ViewGroup,
    width: Int = ScreenUtils.getScreenWidth(this),
    height: Int = ScreenUtils.dpToPx(this, 50f),
    callback: AdFlowCallback? = null,
    onClose: ((Boolean, String?) -> Unit)? = null
) {
    AdKit.instance.showBannerAd(
        this,
        adId,
        container,
        width,
        height,
        wrapAdCloseCallback(callback, onClose)
    )
}

/**
 * Fragment展示Banner广告扩展
 */
fun Fragment.showBannerAd(
    adId: String,
    container: ViewGroup,
    width: Int = ScreenUtils.getScreenWidth(requireContext()),
    height: Int = ScreenUtils.dpToPx(requireContext(), 50f),
    callback: AdFlowCallback? = null,
    onClose: ((Boolean, String?) -> Unit)? = null
) {
    AdKit.instance.showBannerAd(
        requireActivity(),
        adId,
        container,
        width,
        height,
        wrapAdCloseCallback(callback, onClose)
    )
}

/**
 * Activity展示信息流广告扩展
 */
fun FragmentActivity.showNativeAd(
    adId: String,
    container: ViewGroup,
    height: Int,
    width: Int = ScreenUtils.getScreenWidth(this),
    callback: AdFlowCallback? = null,
    onClose: ((Boolean, String?) -> Unit)? = null
) {
    AdKit.instance.showNativeAd(
        this,
        adId,
        container,
        width,
        height,
        wrapAdCloseCallback(callback, onClose)
    )
}

/**
 * Fragment展示信息流广告扩展
 */
fun Fragment.showNativeAd(
    adId: String,
    container: ViewGroup,
    height: Int,
    width: Int = ScreenUtils.getScreenWidth(requireContext()),
    callback: AdFlowCallback? = null,
    onClose: ((Boolean, String?) -> Unit)? = null
) {
    AdKit.instance.showNativeAd(
        requireActivity(),
        adId,
        container,
        width,
        height,
        wrapAdCloseCallback(callback, onClose)
    )
}

/**
 * Activity展示激励视频广告扩展
 */
fun FragmentActivity.showRewardVideo(
    adId: String,
    callback: AdFlowCallback? = null,
    onClose: ((Boolean, String?) -> Unit)? = null
) {
    AdKit.instance.showRewardVideo(
        this,
        adId,
        wrapAdCloseCallback(callback, onClose, isReward = true)
    )
}

/**
 * Fragment展示激励视频广告扩展
 */
fun Fragment.showRewardVideo(
    adId: String,
    callback: AdFlowCallback? = null,
    onClose: ((Boolean, String?) -> Unit)? = null
) {
    AdKit.instance.showRewardVideo(
        requireActivity(),
        adId,
        wrapAdCloseCallback(callback, onClose, isReward = true)
    )
}


/**
 * 切换总次数（进程内累计，达到阈值后清零并展示插屏）
 */
private var switchCount = 0

/**
 * 切换 n 次后展示插屏：每次调用累加一次切换计数，达到 [interval] 时展示并重置计数。
 *
 * @param adId 插屏广告位 id
 * @param interval 触发展示所需的切换次数，默认 3；小于等于 0 时按 1 处理
 */
fun FragmentActivity.showSwitchNInterstitial(
    adId: String,
    interval: Int = 3,
    callback: AdFlowCallback? = null,
    onClose: ((Boolean, String?) -> Unit)? = null
) {
    if (switchCount <= 0 || switchCount % interval != 0) {
        switchCount++
        return
    }
    switchCount = 0
    showInterstitialAd(adId, callback, onClose)
}

/**
 * Fragment：切换 n 次后展示插屏，计数与 [FragmentActivity.showSwitchNInterstitial] 共用。
 */
fun Fragment.showSwitchNInterstitial(
    adId: String,
    interval: Int = 3,
    callback: AdFlowCallback? = null,
    onClose: ((Boolean, String?) -> Unit)? = null
) {
    requireActivity().showSwitchNInterstitial(adId, interval, callback, onClose)
}

/**
 * 展示双插屏
 */
fun FragmentActivity.showDoubleInterstitialAd(firstId: String, secondId: String) {
    showSequentialInterstitialAd(listOf(firstId, secondId))
}

/**
 * 按 [adIds] 顺序依次展示插屏：上一条关闭（或失败结束）后再展示下一条；空列表直接返回。
 */
fun FragmentActivity.showSequentialInterstitialAd(adIds: List<String>) {
    val ids = adIds.filter { it.isNotBlank() }
    if (ids.isEmpty()) return
    fun showAt(index: Int) {
        if (index >= ids.size) return
        showInterstitialAd(ids[index]) { _, _ ->
            showAt(index + 1)
        }
    }
    showAt(0)
}

/**
 * 展示后台插屏：按 [adIds] 顺序依次展示（可只配 1 个或多个）。
 * 当前页为 [SplashBackendAdActivity] 时不展示，避免与后台开屏叠弹。
 * 时长与「开屏结束后再弹」由 [com.adwant.kit.BackendInterstitialWatcher] / [AdKit] 协调。
 */
fun FragmentActivity.showBackendInterstitialAd(adIds: List<String>) {
    if (this is SplashBackendAdActivity) {
        AdKitLog.d("skip showBackendInterstitialAd, on SplashBackendAdActivity")
        return
    }
    showSequentialInterstitialAd(adIds)
}

/**
 * 将失败 / 关闭统一映射到 [onClose]。
 *
 * @param isReward 激励视频关闭时第一个参数为是否获得奖励
 */
internal fun wrapAdCloseCallback(
    callback: AdFlowCallback?,
    onClose: ((Boolean, String?) -> Unit)?,
    isReward: Boolean = false
): AdFlowCallback? {
    if (onClose == null) return callback
    return object : AbsAdFlowCallback() {
        private var finished = false
        private var rewardVerified = false

        private fun dispatchClose(verify: Boolean, msg: String?) {
            if (finished) return
            finished = true
            onClose?.invoke(verify, msg)
        }

        override fun onLoadStart(type: AdType, adId: String) {
            callback?.onLoadStart(type, adId)
        }

        override fun onLoadSuccess(type: AdType, adId: String) {
            callback?.onLoadSuccess(type, adId)
        }

        override fun onLoadFail(type: AdType, adId: String, code: Int?, msg: String?) {
            callback?.onLoadFail(type, adId, code, msg)
            // 不允许展示广告时第一个参数为 true，避免上层把「跳过广告」当成失败阻断
            val success = msg == AdKit.MSG_NOT_ALLOW_SHOW_AD
            dispatchClose(success, msg)
        }

        override fun onRenderSuccess(type: AdType, adId: String) {
            callback?.onRenderSuccess(type, adId)
        }

        override fun onRenderFail(type: AdType, adId: String, code: Int?, msg: String?) {
            callback?.onRenderFail(type, adId, code, msg)
            dispatchClose(false, msg)
        }

        override fun onShow(type: AdType, adId: String) {
            callback?.onShow(type, adId)
        }

        override fun onClick(type: AdType, adId: String) {
            callback?.onClick(type, adId)
        }

        override fun onClose(type: AdType, adId: String) {
            callback?.onClose(type, adId)
            dispatchClose(if (isReward) rewardVerified else false, null)
        }

        override fun onVideoComplete(type: AdType, adId: String) {
            callback?.onVideoComplete(type, adId)
        }

        override fun onVideoError(type: AdType, adId: String) {
            callback?.onVideoError(type, adId)
        }

        override fun onSkipped(type: AdType, adId: String) {
            callback?.onSkipped(type, adId)
        }

        override fun onRewardVerify(
            adId: String,
            verify: Boolean,
            amount: Int,
            name: String?,
            errorCode: Int,
            errorMsg: String?
        ) {
            if (verify) rewardVerified = true
            callback?.onRewardVerify(adId, verify, amount, name, errorCode, errorMsg)
        }

        override fun onRewardArrived(
            adId: String,
            rewardValid: Boolean,
            amount: Int,
            bundle: Bundle?
        ) {
            if (rewardValid) rewardVerified = true
            callback?.onRewardArrived(adId, rewardValid, amount, bundle)
        }
    }
}
