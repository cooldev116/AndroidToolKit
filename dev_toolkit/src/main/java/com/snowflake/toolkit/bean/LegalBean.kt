package com.snowflake.toolkit.bean

data class LegalBean(
    //用户协议标题
    val agreementTitle: String,
    //用户协议链接
    val agreementUrl: String,
    val appName: String,
    val packageName: String,
    //隐私政策标题
    val privacyTitle: String,
    //隐私政策链接
    val privacyUrl: String
)
