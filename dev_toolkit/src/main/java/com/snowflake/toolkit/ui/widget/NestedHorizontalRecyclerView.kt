package com.snowflake.toolkit.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs


/**
 * @description: 为了解决横向滑动列表与ViewPager2滑动冲突
 * @author:Melon
 * @date:2025/6/19
 */
class NestedHorizontalRecyclerView(context: Context, attrs: AttributeSet? = null) :
    RecyclerView(context, attrs) {

    private var startX = 0f
    private var startY = 0f
    private var isDraggingHorizontally = false

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        e?.apply {
            when (actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    startX = x
                    startY = y
                    // 告诉父布局不要拦截事件（暂时）
                    parent.requestDisallowInterceptTouchEvent(true)
                    isDraggingHorizontally = false
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = x - startX
                    val dy = y - startY
                    if (abs(dx.toDouble()) > abs(dy.toDouble())) {
                        // 横向滑动，阻止父类拦截
                        parent.requestDisallowInterceptTouchEvent(true)
                        isDraggingHorizontally = true
                    } else {
                        // 纵向滑动，允许父类拦截（比如可能需要滑动 ViewPager2 中的内容）
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    // 释放时恢复默认
                    parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.onInterceptTouchEvent(e)
    }
}