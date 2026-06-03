package com.snowflake.toolkit.utils

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


/**
 * @description:键盘工具类
 * @author:Melon
 * @date:2025/6/27
 */
object KeyboardUtil {
    /**
     * @description 隐藏键盘
     * @author Melon
     * @time 2025/7/11 16:25
     */
    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * @description 是否需要隐藏键盘
     * @author Melon
     * @time 2025/7/11 16:30
     */
    fun shouldHideKeyboard(v: View, event: MotionEvent): Boolean {
        if (v is EditText) {
            val location = IntArray(2)
            v.getLocationOnScreen(location)
            val left = location[0]
            val top = location[1]
            val right = left + v.getWidth()
            val bottom = top + v.getHeight()
            val x = event.rawX
            val y = event.rawY
            return !(x > left && x < right && y > top && y < bottom)
        }
        return false
    }
}