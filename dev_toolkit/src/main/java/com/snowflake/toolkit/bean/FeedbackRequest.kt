package com.snowflake.toolkit.bean

/**
 * 意见反馈提交请求体。
 *
 * @param content 反馈内容（必填）
 * @param contact 联系方式（选填；有值时需为手机号或邮箱）
 * @param screenshots 截图 URL 列表（选填；暂无上传能力时可传 null）
 */
data class FeedbackRequest(
    val content: String,
    val contact: String? = null,
    val screenshots: List<String>? = null,
)
