package com.adwant.kit.ui

import androidx.viewbinding.ViewBinding
import com.adwant.kit.AdFlowCallback
import com.adwant.kit.AdKit
import com.adwant.kit.AdType
import com.adwant.kit.constant.KEY_AGREE_PRIVACY_POLICY
import com.adwant.kit.showSplashAd
import com.snowflake.toolkit.base.BaseVBMultiActivity
import com.snowflake.toolkit.ext.toast
import com.snowflake.toolkit.utils.MMKVUtil

/**
 * @description:开屏页面封装基类
 * @author:Melon
 * @date:2026/4/27
 */
abstract class SplashStartAdActivity<VB : ViewBinding> : BaseVBMultiActivity<VB>() {

    override fun initView() {
        super.initView()
        val isAgree = MMKVUtil.getBoolean(KEY_AGREE_PRIVACY_POLICY, false)
        if (isAgree) {
            initSDK()
        } else {
            showPrivacyDialog {
                initSDK()
            }
        }
    }

    private fun initSDK() {
        AdKit.instance.init(applicationContext, getAppId()) { isSuccess, msg ->
            if (isSuccess) {
                onInitSDKSuccess()
                startShowSplash()
            } else {
                toast(msg)
                finish()
            }
        }
    }

    /**
     * 展示闪屏广告
     */
    private fun startShowSplash() {
        val adIds = getAdIds()
        if (adIds.isEmpty()) {
            onSplashCompleted()
        }
        if (adIds.size == 1) {
            showSplashAd(adIds[0], object : AdFlowCallback {
                override fun onClose(type: AdType, adId: String) {
                    super.onClose(type, adId)
                    onSplashCompleted()
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
                            onSplashCompleted()
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

    abstract fun getAppId(): String

    /**
     * 展示隐私协议弹框
     */
    abstract fun showPrivacyDialog(next: () -> Unit)

    /**
     * 初始化sdk成功
     */
    fun onInitSDKSuccess() {}

    /**
     * 开屏广告展示完成（包括开关、黑名单等未展示的也会调用此方法）
     */
    fun onSplashCompleted() {}
}