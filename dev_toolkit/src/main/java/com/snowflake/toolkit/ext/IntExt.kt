package com.snowflake.toolkit.ext

/**
 * @description:Int扩展函数
 * @author:Melon
 * @date:2025/7/11
 */

/**
 * @description:补0，一般用于时分秒上，如果是一位的，就进行补0
 * @author Melon
 * @time 2025/7/11 17:11
 */
fun Int.asTwoDight(): String {
    return if (this.toString().length < 2) {
        "0$this"
    } else {
        this.toString()
    }
}

