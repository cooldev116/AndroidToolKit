package com.snowflake.toolkit.net

/**
 * 通用接口响应包装。
 *
 * 普通对象：
 * ```kotlin
 * @GET("banner/json")
 * suspend fun getBanner(): ApiResponse<BannerBean>
 * ```
 *
 * 分页：
 * ```kotlin
 * @GET("article/list")
 * suspend fun getArticles(): ApiResponse<Paging<ArticleBean>>
 * ```
 *
 * 约定：[errorCode] == 0 为业务成功（与玩 Android 等常见风格一致，宿主也可按自身后端调整判断）。
 *
 * @author Melon
 * @date 2026/8/9
 */
data class ApiResponse<T>(
    val errorCode: Int,
    val errorMsg: String?,
    val data: T?,
) {
    /** 是否业务成功 */
    fun isSuccess(): Boolean = errorCode == 0
}
