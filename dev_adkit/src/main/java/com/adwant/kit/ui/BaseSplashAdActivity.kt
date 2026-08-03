package com.adwant.kit.ui

import com.adwant.kit.AdFlowCallback
import com.adwant.kit.AdType
import com.adwant.kit.databinding.KitActivitySplashBinding
import com.adwant.kit.inter.ISplashStyle
import com.adwant.kit.showSplashAd
import com.snowflake.toolkit.base.BaseVBMultiActivity

/**
 * @description:开屏广告页面公共基类（样式绑定 + 广告展示流程）
 * @author:Melon
 * @date:2026/4/27
 */
abstract class BaseSplashAdActivity : BaseVBMultiActivity<KitActivitySplashBinding>() {

    override fun initView() {
        super.initView()
        binding.bindSplashStyle(this, getSplashStyle())
    }

    /**
     * 展示闪屏广告
     */
    protected fun startShowSplash() {
        val adIds = getAdIds()
        if (adIds.isEmpty()) {
            onSplashCompleted()
            return
        }
        if (adIds.size == 1) {
            showSplashAd(adIds[0], object : AdFlowCallback {
                override fun onClose(type: AdType, adId: String) {
                    super.onClose(type, adId)
                    onSplashCompleted()
                }
            })
            return
        }
        showSplashAd(adIds[0], object : AdFlowCallback {
            override fun onClose(type: AdType, adId: String) {
                super.onClose(type, adId)
                showSplashAd(adIds[1], object : AdFlowCallback {
                    override fun onClose(type: AdType, adId: String) {
                        super.onClose(type, adId)
                        onSplashCompleted()
                    }
                })
            }
        })
    }

    /**
     * 获取闪屏页样式配置
     */
    protected abstract fun getSplashStyle(): ISplashStyle

    /**
     * 获取广告id
     */
    protected abstract fun getAdIds(): List<String>

    /**
     * 开屏广告展示完成（包括开关、黑名单等未展示的也会调用此方法）
     */
    protected open fun onSplashCompleted() {}
}
