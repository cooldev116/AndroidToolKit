package com.adwant.kit.utils

import android.app.Activity

/**
 * 判断当前 [Activity] 是否为广告 SDK 拉起的容器页。
 *
 * 穿山甲插屏 / 激励等会新开 Activity（如 Stub_Standard_*）承载广告；
 * 回前台时若栈顶是此类页面，不应再启动后台开屏，避免叠在广告容器上。
 */
internal fun Activity.isAdSdkActivity(): Boolean {
    val name = javaClass.name
    // 穿山甲 / GroMore 主包与 stub 容器
    if (name.startsWith("com.bytedance.sdk.openadsdk")) return true
    // 下载器、落地页等附属 Activity，同样不应触发后台开屏
    if (name.startsWith("com.ss.android.downloadlib")) return true
    if (name.startsWith("com.ss.android.socialbase")) return true
    return false
}
