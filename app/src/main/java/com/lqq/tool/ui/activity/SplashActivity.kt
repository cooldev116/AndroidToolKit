package com.lqq.tool.ui.activity

import com.adwant.kit.inter.ISplashStyle
import com.adwant.kit.ui.SplashStartAdActivity
import com.lqq.tool.impl.SplashStyle
import com.snowflake.toolkit.device.DeviceUdidUtil
import com.snowflake.toolkit.ext.openActivity

class SplashActivity : SplashStartAdActivity() {
    override fun getSplashStyle(): ISplashStyle {
        return SplashStyle()
    }

    override fun getAdIds(): List<String> {
        return listOf("103928850", "103928850")
    }

    override fun getAppId(): String {
        return "5794264"
    }

    override fun showPrivacyDialog(next: () -> Unit) {
        DeviceUdidUtil.initOaid()
        next.invoke()
    }

    override fun onSplashCompleted() {
        super.onSplashCompleted()
        openActivity(MainActivity::class.java)
    }
}