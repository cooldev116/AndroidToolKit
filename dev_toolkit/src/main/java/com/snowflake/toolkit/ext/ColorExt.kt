package com.snowflake.toolkit.ext

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.snowflake.toolkit.manger.ToolKitManager

/**
 * @description:颜色相关的扩展方法
 * @author:Melon
 * @date:2025/6/27
 */
@ColorInt
fun Any.parseColor(color: String): Int {
    require(color.startsWith("#")) { "Color values must start with #" }
    require(color.length == 7 || color.length == 9) { "Wrong color value length" }
    return Color.parseColor(color)
}

@ColorInt
fun Any.getColor(@ColorRes color: Int): Int {
    return ToolKitManager.instance.getContext().run {
        ContextCompat.getColor(this, color)
    }
}