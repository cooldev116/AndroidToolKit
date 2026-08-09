package com.snowflake.toolkit.ui.link

import android.os.Handler
import android.os.Looper
import android.text.Layout
import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.widget.TextView

/**
 * @description: 处理链接按下高亮、单击与长按；每个 TextView 使用独立实例，避免跨控件状态串扰。
 * @author: Melon
 * @date: 2026/8/9
 */
class TouchableLinkMovementMethod : LinkMovementMethod() {

    /** 当前按下的链接。 */
    private var pressedSpan: TouchableLinkSpan? = null

    /** 是否正按在链接上；[LinkConsumableTextView] 据此决定是否消费触摸。 */
    val isLinkPressed: Boolean
        get() = pressedSpan != null

    private val handler = Handler(Looper.getMainLooper())
    private var longPressFired = false

    private val longClickRunnable = Runnable {
        val span = pressedSpan ?: return@Runnable
        val textView = attachedTextView ?: return@Runnable
        longPressFired = true
        if (textView.isHapticFeedbackEnabled) {
            textView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
        span.onLongClick(textView)
        clearPressed(textView.text as? Spannable)
    }

    private var attachedTextView: TextView? = null

    /**
     * 触摸链路：DOWN 定位 Span 并启动长按计时；MOVE 滑出则取消；
     * UP 在未触发长按时回调单击；CANCEL 清理状态。
     */
    override fun onTouchEvent(
        textView: TextView,
        spannable: Spannable,
        event: MotionEvent
    ): Boolean {
        attachedTextView = textView
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                longPressFired = false
                pressedSpan = findPressedSpan(textView, spannable, event)
                val span = pressedSpan
                if (span != null) {
                    span.isTouched = true
                    Selection.setSelection(
                        spannable,
                        spannable.getSpanStart(span),
                        spannable.getSpanEnd(span)
                    )
                    textView.invalidate()
                    handler.postDelayed(longClickRunnable, LONG_PRESS_TIMEOUT_MS)
                    return true
                }
                return false
            }

            MotionEvent.ACTION_MOVE -> {
                val span = pressedSpan ?: return false
                val touched = findPressedSpan(textView, spannable, event)
                if (touched !== span) {
                    cancelLongPress()
                    clearPressed(spannable)
                    textView.invalidate()
                }
                return pressedSpan != null
            }

            MotionEvent.ACTION_UP -> {
                cancelLongPress()
                val span = pressedSpan
                if (span != null && !longPressFired) {
                    span.onClick(textView)
                }
                clearPressed(spannable)
                textView.invalidate()
                // 按下过链接则消费事件，避免父级同时响应
                return span != null || longPressFired
            }

            else -> {
                cancelLongPress()
                clearPressed(spannable)
                textView.invalidate()
                return false
            }
        }
    }

    /** 根据触摸坐标换算到字符 offset，再取该处的 [TouchableLinkSpan]。 */
    private fun findPressedSpan(
        widget: TextView,
        spannable: Spannable,
        event: MotionEvent
    ): TouchableLinkSpan? {
        var x = event.x.toInt() - widget.totalPaddingLeft + widget.scrollX
        var y = event.y.toInt() - widget.totalPaddingTop + widget.scrollY
        val layout: Layout = widget.layout ?: return null
        val line = layout.getLineForVertical(y)
        val off = try {
            layout.getOffsetForHorizontal(line, x.toFloat())
        } catch (_: IndexOutOfBoundsException) {
            return null
        }
        val lineEnd = layout.getLineEnd(line)
        // 行尾附近的 offset 容易偏 1，直接忽略避免误点
        if (off == lineEnd || off == lineEnd - 1) return null
        return spannable.getSpans(off, off, TouchableLinkSpan::class.java).firstOrNull()
    }

    private fun cancelLongPress() {
        handler.removeCallbacks(longClickRunnable)
    }

    private fun clearPressed(spannable: Spannable?) {
        pressedSpan?.isTouched = false
        pressedSpan = null
        if (spannable != null) {
            Selection.removeSelection(spannable)
        }
    }

    companion object {
        private const val LONG_PRESS_TIMEOUT_MS = 500L
    }
}
