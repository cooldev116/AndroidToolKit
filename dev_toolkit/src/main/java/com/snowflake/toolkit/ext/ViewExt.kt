package com.snowflake.toolkit.ext

import android.view.View

/**
 * 上次点击时间
 */
private var lastClickTime = 0L

/**
 * 点击延时时间
 */
const val DELAY = 500L

/**
 * View的Hash值
 */
private var hashCode = 0

/**
 * 防止用户多次点击的扩展函数
 *
 * @param action 点击按钮要处理的逻辑
 */
infix fun View.click(action: () -> Unit) {
    this.setOnClickListener {
        if (this.hashCode() != hashCode) {
            hashCode = this.hashCode()
            lastClickTime = System.currentTimeMillis()
            action()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime !in 0..DELAY) {
                lastClickTime = currentTime
                action()
            }
        }
    }
}