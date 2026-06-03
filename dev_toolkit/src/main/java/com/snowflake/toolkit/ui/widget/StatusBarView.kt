package com.snowflake.toolkit.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * 状态栏填充View
 */
class StatusBarView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val statusBarHeight = getStatusBarHeight()
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        // 根据测量模式来设置宽度
        // 设置高度为布局文件中设置的高度
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, statusBarHeight)
    }

    /**
     * 获取状态栏的高度
     */
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}