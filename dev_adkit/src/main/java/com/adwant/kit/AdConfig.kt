package com.adwant.kit

/**
 * @description:广告配置
 * @author:Melon
 * @date:2026/5/18
 */
object AdConfig {
    /**
     * 最大展示插屏数量
     */
    const val DEFAULT_MAX_INTERSTITIAL = 10

    /**
     * 后台开屏：退出后台后需停留的最短时长（毫秒），达到后回前台才展示
     */
    const val BACKEND_SPLASH_THRESHOLD_MS = 5_000L
}