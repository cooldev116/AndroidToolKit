package com.adwant.kit.ui

import androidx.viewbinding.ViewBinding
import com.adwant.kit.AdFlowCallback
import com.adwant.kit.AdType
import com.adwant.kit.showSplashAd
import com.snowflake.toolkit.base.BaseVBMultiActivity

/**
 * @description:开屏页面封装基类
 * @author:Melon
 * @date:2026/4/27
 */
abstract class SplashBackendAdActivity<VB : ViewBinding> : BaseVBMultiActivity<VB>() {

    override fun initView() {
        startShowSplash()
    }

    /**
     * 展示闪屏广告
     */
    private fun startShowSplash() {
        val adIds = getAdIds()
        if (adIds.isEmpty()) {
            finish()
        }
        if (adIds.size == 1) {
            showSplashAd(adIds[0], object : AdFlowCallback {
                override fun onClose(type: AdType, adId: String) {
                    super.onClose(type, adId)
                    finish()
                }
            })
        }
        if (adIds.size >= 2) {
            showSplashAd(adIds[0], object : AdFlowCallback {
                override fun onClose(type: AdType, adId: String) {
                    super.onClose(type, adId)
                    showSplashAd(adIds[1], object : AdFlowCallback {
                        override fun onClose(type: AdType, adId: String) {
                            super.onClose(type, adId)
                            finish()
                        }
                    })
                }
            })
        }
    }

    /**
     * 获取广告id
     */
    abstract fun getAdIds(): List<String>
}