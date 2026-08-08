package com.lqq.tool.ui.activity

import com.adwant.kit.inter.ISplashStyle
import com.adwant.kit.ui.SplashBackendAdActivity
import com.lqq.tool.impl.SplashStyle

/**
 * 后台开屏（热启动）页面，由 AdKit 在退出后台达到阈值后自动拉起。
 */
class SplashBackendActivity : SplashBackendAdActivity() {

    override fun getSplashStyle(): ISplashStyle {
        return SplashStyle()
    }

    override fun getAdIds(): List<String> {
        return listOf("103928850", "103928850")
    }
}
