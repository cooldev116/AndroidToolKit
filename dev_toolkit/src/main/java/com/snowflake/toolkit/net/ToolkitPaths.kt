package com.snowflake.toolkit.net

/**
 * Toolkit 网络接口 path 常量，供 Api 声明与 [HeaderInterceptor] 共用，避免两处字符串漂移。
 */
object ToolkitPaths {
    /** 隐私政策 / 用户协议 */
    const val LEGAL = "/api/v1/app/legal"

    /** 意见反馈提交 */
    const val FEEDBACK = "/api/v1/app/feedback"
}
