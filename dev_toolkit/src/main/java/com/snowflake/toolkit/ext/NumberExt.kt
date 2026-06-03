package com.snowflake.toolkit.ext

/**
 * @description:数字相关的扩展类
 * @author:Melon
 * @date:2025/5/27
 */

/**
 * @description 数字格式化，可以用于比如点赞、收藏等超过一定数量需要转换成以w等单位结尾的情景
 * @param suffix 后缀 默认以w结尾
 * @param decimalPlace 保留几位小数，默认保留1位小数
 * @return 转换后的数字字符串
 * @author Melon
 * @time 2025/5/27 16:29
 */
fun Int?.formatNumber(suffix: String = "w", decimalPlace: Int = 1): String {
    return if (this == null) {
        "0"
    } else if (this < 10000) {
        this.toString()
    } else {
        val w = this / 10000.0
        "%.${decimalPlace}f${suffix}".format(w)
    }
}