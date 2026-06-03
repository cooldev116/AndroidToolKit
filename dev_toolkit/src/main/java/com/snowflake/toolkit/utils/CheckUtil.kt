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
     * @description 是否是有效的网页链接
     * @author Melon
     * @time 2025/11/13 19:51
     */
    fun isWebUrl(url: String): Boolean {
        return Regex(REGEX_WEB).matches(url)
    }
}