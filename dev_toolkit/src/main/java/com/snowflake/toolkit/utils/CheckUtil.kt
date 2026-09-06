package com.snowflake.toolkit.utils

/**
 * @description:检测工具类
 * @author:Melon
 * @date:2025/9/23
 */
object CheckUtil {
    /**
     * 手机号校验正则
     */
    private const val REGEX_PHONE = "^1[3-9]\\d{9}\$"

    /**
     * 邮箱校验正则（常见格式即可，不做严格 RFC）
     */
    private const val REGEX_EMAIL =
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"

    /**
     * 网页链接正则校验
     */
    private const val REGEX_WEB =
        "^(https?|ftp)://[\\w\\-]+(\\.[\\w\\-]+)+([\\w.,@?^=%&:/~+#\\-]*[\\w@?^=%&/~+#\\-])?$"

    /**
     * @description 是否是有效的手机号
     * @author Melon
     * @time 2025/9/23 14:44
     */
    fun isValidPhone(phone: String): Boolean {
        return Regex(REGEX_PHONE).matches(phone)
    }

    /**
     * 是否是有效邮箱。
     */
    fun isValidEmail(email: String): Boolean {
        return Regex(REGEX_EMAIL).matches(email)
    }

    /**
     * 联系方式校验：允许为空；非空时须为手机号或邮箱。
     * 用于意见反馈等选填联系方式场景。
     */
    fun isValidContact(contact: String?): Boolean {
        val value = contact?.trim().orEmpty()
        if (value.isEmpty()) return true
        return isValidPhone(value) || isValidEmail(value)
    }

    /**
     * @description 是否是有效的网页链接
     * @author Melon
     * @time 2025/11/13 19:51
     */
    fun isWebUrl(url: String): Boolean {
        return Regex(REGEX_WEB).matches(url)
    }
}