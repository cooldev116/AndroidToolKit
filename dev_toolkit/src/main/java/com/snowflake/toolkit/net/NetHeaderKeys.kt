package com.snowflake.toolkit.net

/**
 * 公共请求头字段名，与后端约定保持一致。
 *
 * @author Melon
 * @date 2026/8/9
 */
object NetHeaderKeys {
    const val PACKAGE_NAME = "packageName"
    const val VERSION = "version"
    const val VERSION_CODE = "versionCode"
    const val CHANNEL = "channel"
    const val UDID = "udid"
    const val PLATFORM = "platform"
    const val AUTHORIZATION = "Authorization"

    /** 固定平台标识 */
    const val PLATFORM_ANDROID = "android"

    /** Bearer 方案前缀，与 token 之间保留一个空格 */
    const val BEARER_PREFIX = "Bearer "
}
