package com.adwant.kit.ui

/**
 * @description:热启动开屏页面封装基类
 * @author:Melon
 * @date:2026/4/27
 */
abstract class SplashBackendAdActivity : BaseSplashAdActivity() {

    override fun initView() {
        super.initView()
        startShowSplash()
    }

    /**
     * 开屏广告展示完成（包括开关、黑名单等未展示的也会调用此方法）
     * 默认是当前页面finish掉
     */
    override fun onSplashCompleted() {
        finish()
    }
}
