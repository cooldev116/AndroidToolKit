package com.lqq.tool

import android.app.Application
import android.os.Build
import android.webkit.WebView
import com.adwant.kit.AdKit


/**
 * @description:
 * @author:Melon
 * @date:2025/9/13
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        initAd()
        initWeb()
    }

    private fun initAd() {
        AdKit.instance.init(this, "5794264", isDebug = true)
    }

    private fun initWeb() {
        //Android 9及以上必须设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName()
            if (packageName != processName) {
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }
}