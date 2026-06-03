package com.snowflake.toolkit.ext

import android.os.Build
import com.snowflake.toolkit.manger.ToolKitManager

/**
 * @description:尺寸相关的扩展函数
 * @author:Melon
 * @date:2025/5/30
 */
fun Float.dp2Px(): Int {
    val scale: Float = ToolKitManager.instance.getContext().applicationContext.resources
        .displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

fun Float.sp2Px(): Int {
    val context = ToolKitManager.instance.getContext()
    val metrics = context.resources.displayMetrics
    val fontScale = if (Build.VERSION.SDK_INT >= 34) {
        context.resources.configuration.fontScale
    } else {
        metrics.scaledDensity / metrics.density
    }
    return (this * metrics.density * fontScale).toInt()
}

fun Int.px2Dp(): Float {
    val density = ToolKitManager.instance.getContext().resources.displayMetrics.density
    return this / density
}

fun Int.px2Sp(): Float {
    val context = ToolKitManager.instance.getContext()
    val metrics = context.resources.displayMetrics
    val fontScale = if (Build.VERSION.SDK_INT >= 34) {
        context.resources.configuration.fontScale
    } else {
        metrics.scaledDensity / metrics.density
    }
    return this / (metrics.density * fontScale)
}