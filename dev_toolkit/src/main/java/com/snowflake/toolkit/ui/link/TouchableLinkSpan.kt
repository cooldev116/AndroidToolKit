package com.snowflake.toolkit.ui.link

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.View
import com.snowflake.toolkit.R

/**
 * @description: 可触摸高亮的链接 Span，负责绘制样式并转发单击/长按。
 * @author: Melon
 * @date: 2026/8/9
 */
internal class TouchableLinkSpan(
    context: Context,
    private val link: TextLink
) : ClickableSpan() {

    var isTouched: Boolean = false

    private val textColor: Int
    private val textColorOfHighlightedLink: Int

    init {
        textColor = if (link.textColor == 0) {
            resolveThemeColor(context, R.styleable.TextLinkBuilder_defaultLinkColor)
        } else {
            link.textColor
        }

        textColorOfHighlightedLink = when {
            link.textColorOfHighlightedLink != 0 -> link.textColorOfHighlightedLink
            else -> {
                val themeColor = resolveThemeColor(
                    context,
                    R.styleable.TextLinkBuilder_defaultTextColorOfHighlightedLink
                )
                // 主题未配置高亮色时，沿用普通链接色，避免继续落回默认浅蓝
                if (themeColor == TextLink.DEFAULT_COLOR) textColor else themeColor
            }
        }
    }

    override fun onClick(widget: View) {
        val clicked = link.text ?: return
        link.clickListener?.onClick(clicked)
    }

    fun onLongClick(widget: View) {
        val clicked = link.text ?: return
        link.longClickListener?.onLongClick(clicked)
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = link.underlined
        ds.isFakeBoldText = link.bold
        ds.color = if (isTouched) textColorOfHighlightedLink else textColor
        ds.bgColor = if (isTouched) {
            adjustAlpha(textColor, link.highlightAlpha)
        } else {
            Color.TRANSPARENT
        }
        link.typeface?.let { ds.typeface = it }
    }

    /** 按系数缩放颜色 alpha，用于按下时的背景高亮。 */
    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).toInt().coerceIn(0, 255)
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    /** 从主题 style（[R.attr.textLinkBuilderStyle]）读取默认链接色。 */
    private fun resolveThemeColor(context: Context, index: Int): Int {
        val array = obtainStyledAttrsFromThemeAttr(
            context,
            R.attr.textLinkBuilderStyle,
            R.styleable.TextLinkBuilder
        )
        val color = array.getColor(index, TextLink.DEFAULT_COLOR)
        array.recycle()
        return color
    }

    private fun obtainStyledAttrsFromThemeAttr(
        context: Context,
        themeAttr: Int,
        styleAttrs: IntArray
    ): TypedArray {
        val outValue = TypedValue()
        context.theme.resolveAttribute(themeAttr, outValue, true)
        return context.obtainStyledAttributes(outValue.resourceId, styleAttrs)
    }
}
