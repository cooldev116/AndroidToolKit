package com.adwant.kit.ui

import com.adwant.kit.AdKit

/**
 * @description:热启动开屏页面封装基类
 * @author:Melon
 * @date:2026/4/27
 *
 * finish 后通过 [AdKit.onBackendSplashFinished] 放行挂起的后台插屏。
 */
abstract class SplashBackendAdActivity : BaseSplashAdActivity() {

    /** 避免 onSplashCompleted 与 onDestroy 重复通知 */
    private var splashFinishNotified = false

    override fun initView() {
        super.initView()
        startFakeProgress()
        startShowSplash()
    }

    /**
     * 开屏广告展示完成（包括开关、黑名单等未展示的也会调用此方法）
     * 默认是当前页面finish掉
     */
    override fun onSplashCompleted() {
        finish()
    }

    override fun onDestroy() {
        // 配置变更重建时不视为本轮开屏结束，避免误弹插屏
        if (!isChangingConfigurations) {
            notifyBackendSplashFinishedIfNeeded()
        }
        super.onDestroy()
    }

    /**
     * 通知 AdKit 本轮后台开屏已结束，从而执行挂起的后台插屏。
     */
    private fun notifyBackendSplashFinishedIfNeeded() {
        if (splashFinishNotified) return
        splashFinishNotified = true
        AdKit.instance.onBackendSplashFinished()
    }
}
