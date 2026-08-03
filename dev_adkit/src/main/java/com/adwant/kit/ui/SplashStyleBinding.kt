package com.adwant.kit.ui

import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.adwant.kit.R
import com.adwant.kit.databinding.KitActivitySplashBinding
import com.adwant.kit.inter.ISplashStyle

internal fun KitActivitySplashBinding.bindSplashStyle(
    activity: FragmentActivity,
    style: ISplashStyle
) {
    ivSplash.setImageResource(style.getSplashBgImg())
    val bgColor = ContextCompat.getColor(activity, style.getSplashProgressBgColor())
    val progressColor = ContextCompat.getColor(activity, style.getSplashProgressColor())
    val drawable = ContextCompat.getDrawable(activity, R.drawable.kit_splash_progress)
        ?.mutate() as? LayerDrawable ?: return
    (drawable.findDrawableByLayerId(android.R.id.background) as? GradientDrawable)
        ?.setColor(bgColor)
    when (val progress = drawable.findDrawableByLayerId(android.R.id.progress)) {
        is ScaleDrawable -> (progress.drawable as? GradientDrawable)?.setColor(progressColor)
        is ClipDrawable -> (progress.drawable as? GradientDrawable)?.setColor(progressColor)
        is GradientDrawable -> progress.setColor(progressColor)
    }
    pbSplash.progressDrawable = drawable
}
