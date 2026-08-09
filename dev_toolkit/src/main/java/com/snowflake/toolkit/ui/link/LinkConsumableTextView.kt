package com.snowflake.toolkit.ui.link

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

/**
 * @description: 仅在点中链接时消费触摸，未点中则交给父级（ListView/RecyclerView item 点击可用）。
 * 普通 TextView + LinkMovementMethod 会吞掉全部触摸，导致条目点击失效。
 * @author: Melon
 * @date: 2026/8/9
 */
class LinkConsumableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    /** 当前手势是否从链接上按下；贯穿 MOVE/UP，避免 UP 时 pressedSpan 已清空导致误传给父级。 */
    private var touchStartedOnLink = false

    override fun hasFocusable(): Boolean = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                super.onTouchEvent(event)
                val method = movementMethod
                touchStartedOnLink =
                    method is TouchableLinkMovementMethod && method.isLinkPressed
                return touchStartedOnLink
            }

            MotionEvent.ACTION_MOVE -> {
                super.onTouchEvent(event)
                return touchStartedOnLink
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                super.onTouchEvent(event)
                val consume = touchStartedOnLink
                touchStartedOnLink = false
                return consume
            }

            else -> return super.onTouchEvent(event)
        }
    }
}
