package com.snowflake.toolkit.ext

import android.widget.TextView

/**
 * @description:TextView扩展方法
 * @author:Melon
 * @date:2025/6/4
 */

fun TextView.txt(context: String?) {
    this.text = context ?: ""
}