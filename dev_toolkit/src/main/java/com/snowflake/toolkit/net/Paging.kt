package com.snowflake.toolkit.net

/**
 * 通用分页结构，作为 [ApiResponse.data] 的泛型参数使用：
 * `ApiResponse<Paging<Bean>>`。
 *
 * @author Melon
 * @date 2026/8/9
 */
data class Paging<T>(
    val page: Int,
    val pageSize: Int,
    val totalSize: Int,
    val list: List<T>?,
) {
    /** 当前页数据，null 时视为空列表，便于 UI 直接遍历 */
    fun items(): List<T> = list.orEmpty()

    /** 是否还有下一页（按已加载条数与总量估算） */
    fun hasMore(): Boolean {
        if (totalSize <= 0) return false
        val loaded = page * pageSize
        return loaded < totalSize
    }
}
