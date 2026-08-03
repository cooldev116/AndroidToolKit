package com.adwant.kit.inter

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.adwant.kit.R

interface ISplashStyle {
    /**
     * 获取闪屏页背景图片
     */
    @DrawableRes
    fun getSplashBgImg(): Int

    /**
     * 获取进度条背颜色
     */
    @ColorRes
    fun getSplashProgressBgColor(): Int {
        return R.color.kit_splash_progress_bg_color
    }

    /**
     * 获取进度条进度颜色
     */
    @ColorRes
    fun getSplashProgressColor(): Int {
        return R.color.kit_splash_progress_color
    }
}