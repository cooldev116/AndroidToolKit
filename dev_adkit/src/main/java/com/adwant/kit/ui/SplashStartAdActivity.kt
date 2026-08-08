package com.adwant.kit.ui

import android.os.Handler
import android.os.Looper
import com.adwant.kit.AdKit
import com.adwant.kit.BuildConfig
import com.adwant.kit.constant.KEY_AGREE_PRIVACY_POLICY
import com.snowflake.toolkit.ext.toast
import com.snowflake.toolkit.utils.MMKVUtil

/**
 * @description:冷启动开屏页面封装基类
 * @author:Melon
 * @date:2026/4/27
 */
abstract class SplashStartAdActivity : BaseSplashAdActivity() {

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun initView() {
        super.initView()
        val isAgree = MMKVUtil.getBoolean(KEY_AGREE_PRIVACY_POLICY, false)
        if (isAgree) {
            startFakeProgress()
            initSDK()
        } else {
            showPrivacyDialog {
                startFakeProgress()
                initSDK()
            }
        }
    }

    private fun initSDK() {
        AdKit.instance.init(
            applicationContext,
            getAppId(),
            isDebug = BuildConfig.DEBUG
        ) { isSuccess, msg ->
            // 穿山甲 init 回调可能在子线程，UI / Lifecycle 相关操作切回主线程
            mainHandler.post {
                if (isSuccess) {
                    enableBackendSplashIfNeeded()
                    onInitSDKSuccess()
                    startShowSplash()
                } else {
                    toast(msg)
                    finish()
                }
            }
        }
    }

    private fun enableBackendSplashIfNeeded() {
        val splashClass = getBackendSplashActivityClass() ?: return
        AdKit.instance.enableBackendSplash(application, splashClass)
    }

    protected abstract fun getAppId(): String

    /**
     * 后台开屏 Activity（[SplashBackendAdActivity] 实现类）。
     * 返回非 null 时，SDK 初始化成功后会自动启用后台开屏监听。
     */
    protected open fun getBackendSplashActivityClass(): Class<out SplashBackendAdActivity>? = null

    /**
     * 展示隐私协议弹框
     */
    protected abstract fun showPrivacyDialog(next: () -> Unit)

    /**
     * 初始化sdk成功
     */
    protected open fun onInitSDKSuccess() {}
}
