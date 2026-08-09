package com.lqq.tool.bean

/**
 * 首页 Banner 条目，对应 [https://www.wanandroid.com/banner/json](https://www.wanandroid.com/banner/json)。
 *
 * @author Melon
 * @date 2026/8/9
 */
data class BannerBean(
    val desc: String?,
    val id: Int,
    val imagePath: String?,
    val isVisible: Int,
    val order: Int,
    val title: String?,
    val type: Int,
    val url: String?,
)
