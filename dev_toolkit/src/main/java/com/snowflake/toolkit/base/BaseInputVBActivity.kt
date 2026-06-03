package com.snowflake.toolkit.base

import android.view.MotionEvent
import androidx.viewbinding.ViewBinding
import com.snowflake.toolkit.utils.KeyboardUtil

/**
 * @description:当页面有输入框是，点击非输入框区域，键盘自动收起并失去焦点Activity基类
 * @author:Melon
 * @date:2025/7/11
 */
abstract class BaseInputVBActivity<VB : ViewBinding> : BaseVBActivity<VB>() {
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.apply {
            if (action == MotionEvent.ACTION_DOWN) {
                currentFocus?.let {
                    if (KeyboardUtil.shouldHideKeyboard(it, this)) {
                        KeyboardUtil.hideKeyboard(applicationContext, it)
                        it.clearFocus()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}