package com.adwant.kit

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.adwant.kit.utils.ScreenUtils

/**
 * 广告展示扩展方法
 */

/**
 * 闪屏广告
 */
fun FragmentActivity.showSplashAd(adId: String, callback: AdFlowCallback? = null) {
    AdKit.instance.showSplashAd(this, adId, callback)
}

/**
 * 闪屏广告
 */
fun Fragment.showSplashAd(adId: String, callback: AdFlowCallback? = null) {
    AdKit.instance.showSplashAd(requireActivity(), adId, callback)
}

/**
 * Activity展示插屏广告扩展
 */
fun FragmentActivity.showInterstitialAd(adId: String, callback: AdFlowCallback? = null) {
    AdKit.instance.showInterstitialAd(this, adId, callback)
}

/**
 * Fragment展示插屏广告扩展
 */
fun Fragment.showInterstitialAd(adId: String, callback: AdFlowCallback? = null) {
    AdKit.instance.showInterstitialAd(requireActivity(), adId, callback)
}

/**
 * Activity展示Banner广告扩展
 */
fun FragmentActivity.showBannerAd(
    adId: String, container: ViewGroup,
    width: Int = ScreenUtils.getScreenWidth(this),
    height: Int = ScreenUtils.dpToPx(this, 50f),
    callback: AdFlowCallback? = null
) {
    AdKit.instance.showBannerAd(this, adId, container, width, height, callback)
}

/**
 * Fragment展示Banner广告扩展
 */
fun Fragment.showBannerAd(
    adId: String, container: ViewGroup,
    width: Int = ScreenUtils.getScreenWidth(requireContext()),
    height: Int = ScreenUtils.dpToPx(requireContext(), 50f),
    callback: AdFlowCallback? = null
) {
    AdKit.instance.showBannerAd(requireActivity(), adId, container, width, height, callback)
}

/**
 * Activity展示信息流广告扩展
 */
fun FragmentActivity.showNativeAd(
    adId: String,
    container: ViewGroup,
    width: Int = ScreenUtils.getScreenWidth(this),
    height: Int,
    callback: AdFlowCallback? = null
) {
    AdKit.instance.showNativeAd(this, adId, container, width, height, callback)
}

/**
 * Fragment展示信息流广告扩展
 */
fun Fragment.showNativeAd(
    adId: String,
    container: ViewGroup,
    width: Int = ScreenUtils.getScreenWidth(requireContext()),
    height: Int,
    callback: AdFlowCallback? = null
) {
    AdKit.instance.showNativeAd(requireActivity(), adId, container, width, height, callback)
}