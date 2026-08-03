package com.adwant.kit.ui

import com.adwant.kit.AdKit
import com.adwant.kit.constant.KEY_AGREE_PRIVACY_POLICY
import com.snowflake.toolkit.ext.toast
import com.snowflake.toolkit.utils.MMKVUtil

/**
 * @description:冷启动开屏页面封装基类
 * @author:Melon
 * @date:2026/4/27
 */
abstract class SplashStartAdActivity : BaseSplashAdActivity() {

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

    protected abstract fun getAppId(): String

    /**
     * 展示隐私协议弹框
     */
    protected abstract fun showPrivacyDialog(next: () -> Unit)

    /**
     * 初始化sdk成功
     */
    protected open fun onInitSDKSuccess() {}
}
