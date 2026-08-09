package com.snowflake.toolkit.ui.link

import android.graphics.Color
import android.graphics.Typeface
import java.util.regex.Pattern

/**
 * @description: TextView 可点击链接规则，支持固定文案或正则匹配。
 * 用法类似 Android-TextView-LinkBuilder：配置颜色、下划线、点击/长按后再交给 [TextLinkBuilder] 应用。
 * @author: Melon
 * @date: 2026/8/9
 */
class TextLink {

    var text: String? = null
        private set
    var pattern: Pattern? = null
        private set
    var prependedText: String? = null
        private set
    var appendedText: String? = null
        private set
    var textColor: Int = 0
        private set
    var textColorOfHighlightedLink: Int = 0
        private set
    var highlightAlpha: Float = DEFAULT_ALPHA
        private set
    var underlined: Boolean = true
        private set
    var bold: Boolean = false
        private set
    var typeface: Typeface? = null
        private set
    var clickListener: OnClickListener? = null
        private set
    var longClickListener: OnLongClickListener? = null
        private set

    /**
     * 基于已有规则复制一份，便于正则展开成多条具体匹配时复用样式与回调。
     */
    constructor(other: TextLink) {
        text = other.text
        pattern = other.pattern
        prependedText = other.prependedText
        appendedText = other.appendedText
        textColor = other.textColor
        textColorOfHighlightedLink = other.textColorOfHighlightedLink
        highlightAlpha = other.highlightAlpha
        underlined = other.underlined
        bold = other.bold
        typeface = other.typeface
        clickListener = other.clickListener
        longClickListener = other.longClickListener
    }

    /** 匹配固定字符串。 */
    constructor(text: String) {
        this.text = text
        this.pattern = null
    }

    /** 匹配正则；构建时会展开为多条具体 [text] 规则。 */
    constructor(pattern: Pattern) {
        this.pattern = pattern
        this.text = null
    }

    fun setText(text: String): TextLink {
        this.text = text
        this.pattern = null
        return this
    }

    fun setPattern(pattern: Pattern): TextLink {
        this.pattern = pattern
        this.text = null
        return this
    }

    /** 匹配成功后，在链接文案前插入的前缀（会同步改写 TextView 文本）。 */
    fun setPrependedText(text: String): TextLink {
        this.prependedText = text
        return this
    }

    /** 匹配成功后，在链接文案后插入的后缀（会同步改写 TextView 文本）。 */
    fun setAppendedText(text: String): TextLink {
        this.appendedText = text
        return this
    }

    fun setOnClickListener(listener: OnClickListener): TextLink {
        this.clickListener = listener
        return this
    }

    fun setOnClickListener(listener: (String) -> Unit): TextLink {
        this.clickListener = OnClickListener { listener(it) }
        return this
    }

    fun setOnLongClickListener(listener: OnLongClickListener): TextLink {
        this.longClickListener = listener
        return this
    }

    fun setOnLongClickListener(listener: (String) -> Unit): TextLink {
        this.longClickListener = OnLongClickListener { listener(it) }
        return this
    }

    fun setTextColor(color: Int): TextLink {
        this.textColor = color
        return this
    }

    fun setTextColorOfHighlightedLink(color: Int): TextLink {
        this.textColorOfHighlightedLink = color
        return this
    }

    fun setUnderlined(underlined: Boolean): TextLink {
        this.underlined = underlined
        return this
    }

    fun setBold(bold: Boolean): TextLink {
        this.bold = bold
        return this
    }

    /**
     * 按下时背景高亮的透明度系数，作用在链接文字颜色的 alpha 上。
     * @param alpha 建议 0f~1f，默认 [DEFAULT_ALPHA]
     */
    fun setHighlightAlpha(alpha: Float): TextLink {
        this.highlightAlpha = alpha
        return this
    }

    fun setTypeface(typeface: Typeface): TextLink {
        this.typeface = typeface
        return this
    }

    fun interface OnClickListener {
        fun onClick(clickedText: String)
    }

    fun interface OnLongClickListener {
        fun onLongClick(clickedText: String)
    }

    companion object {
        val DEFAULT_COLOR: Int = Color.parseColor("#33B5E5")
        const val DEFAULT_ALPHA = 0.20f
    }
}
