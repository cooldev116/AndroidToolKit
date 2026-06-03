package com.snowflake.toolkit.ui.widget

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet

/**
 * @description:带有中划线的TextView
 * @author:Melon
 * @date:2025/6/13
 */
class StrikeThruTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0
) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs, def) {
    init {
        paint.apply {
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            paint.isAntiAlias = true
        }
    }
}