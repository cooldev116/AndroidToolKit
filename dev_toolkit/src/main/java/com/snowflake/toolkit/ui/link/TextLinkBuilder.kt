package com.snowflake.toolkit.ui.link

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.widget.TextView
import java.util.regex.Pattern

/**
 * @description: 将 [TextLink] 规则应用到 TextView / CharSequence，生成可点击、可高亮的富文本。
 * @author: Melon
 * @date: 2026/8/9
 *
 * 示例：
 * ```
 * TextLinkBuilder.on(textView)
 *     .addLink(TextLink("用户协议").setOnClickListener { ... })
 *     .build()
 * ```
 */
class TextLinkBuilder private constructor(
    private val type: Int
) {

    private var context: Context? = null
    private var textView: TextView? = null
    private var text: CharSequence? = null
    private var findOnlyFirstMatch = false
    private var spannable: SpannableString? = null
    private val links = ArrayList<TextLink>()

    fun setTextView(textView: TextView): TextLinkBuilder {
        this.textView = textView
        return setText(textView.text)
    }

    fun setText(text: CharSequence): TextLinkBuilder {
        this.text = text
        return this
    }

    fun setContext(context: Context): TextLinkBuilder {
        this.context = context
        return this
    }

    /** 每条规则只匹配第一次出现（含正则展开）。 */
    fun setFindOnlyFirstMatchesForAnyLink(findOnlyFirst: Boolean): TextLinkBuilder {
        this.findOnlyFirstMatch = findOnlyFirst
        return this
    }

    fun addLink(link: TextLink): TextLinkBuilder {
        links.add(link)
        return this
    }

    fun addLinks(linkList: List<TextLink>): TextLinkBuilder {
        require(linkList.isNotEmpty()) { "link list is empty" }
        links.addAll(linkList)
        return this
    }

    /**
     * 执行匹配并生成 Spannable。
     * - [TYPE_TEXT_VIEW]：直接写入 TextView，并设置 [TouchableLinkMovementMethod]
     * - [TYPE_TEXT]：仅返回 CharSequence，调用方需自行 setText + movementMethod
     * @return 无有效链接时返回原始文本；有链接时返回 Spannable
     */
    fun build(): CharSequence {
        val source = text ?: ""
        expandPatternsToLinks()
        if (links.isEmpty()) {
            return source
        }
        applyAppendedAndPrependedText()
        spannable = SpannableString.valueOf(text)
        for (link in links) {
            addLinkToSpan(spannable!!, link)
        }
        if (type == TYPE_TEXT_VIEW) {
            val tv = textView ?: return spannable!!
            tv.text = spannable
            ensureMovementMethod(tv)
        }
        return spannable!!
    }

    /** 正则规则展开为具体字符串规则后，从列表移除 pattern 规则本身。 */
    private fun expandPatternsToLinks() {
        var i = 0
        while (i < links.size) {
            val link = links[i]
            val pattern = link.pattern
            if (pattern != null) {
                addLinksFromPattern(link, pattern)
                links.removeAt(i)
            } else {
                i++
            }
        }
    }

    private fun addLinksFromPattern(template: TextLink, pattern: Pattern) {
        val source = text ?: return
        val matcher = pattern.matcher(source)
        while (matcher.find()) {
            val matched = source.subSequence(matcher.start(), matcher.end()).toString()
            links.add(TextLink(template).setText(matched))
            if (findOnlyFirstMatch) break
        }
    }

    /**
     * 前缀/后缀会改写整段文案中对应匹配项，再更新 link.text，保证后续 span 区间与展示一致。
     * 注意：原库 appended 拼接有误，这里使用 appendedText。
     */
    private fun applyAppendedAndPrependedText() {
        for (i in links.indices) {
            val link = links[i]
            val linkText = link.text ?: continue
            if (link.prependedText != null) {
                val total = "${link.prependedText} $linkText"
                text = TextUtils.replace(text, arrayOf(linkText), arrayOf(total))
                links[i].setText(total)
            }
            val currentText = links[i].text ?: continue
            if (link.appendedText != null) {
                val total = "$currentText ${link.appendedText}"
                text = TextUtils.replace(text, arrayOf(currentText), arrayOf(total))
                links[i].setText(total)
            }
        }
    }

    private fun addLinkToSpan(s: Spannable, link: TextLink) {
        val linkText = link.text ?: return
        val matcher = Pattern.compile(Pattern.quote(linkText)).matcher(text ?: return)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            applyLink(link, start, end, s)
            if (findOnlyFirstMatch) break
        }
    }

    /**
     * 区间已有 Span 时：仅当新区间完全覆盖旧区间才替换，避免半交叉覆盖导致点击错乱。
     */
    private fun applyLink(link: TextLink, start: Int, end: Int, text: Spannable) {
        val ctx = context ?: return
        val existing = text.getSpans(start, end, TouchableLinkSpan::class.java)
        if (existing.isEmpty()) {
            text.setSpan(
                TouchableLinkSpan(ctx, link),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return
        }
        var newConsumesAllOld = true
        for (span in existing) {
            val oldStart = text.getSpanStart(span)
            val oldEnd = text.getSpanEnd(span)
            if (start > oldStart || end < oldEnd) {
                newConsumesAllOld = false
                break
            }
            text.removeSpan(span)
        }
        if (newConsumesAllOld) {
            text.setSpan(
                TouchableLinkSpan(ctx, link),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun ensureMovementMethod(textView: TextView) {
        if (!textView.linksClickable) return
        if (textView.movementMethod !is TouchableLinkMovementMethod) {
            textView.movementMethod = TouchableLinkMovementMethod()
        }
    }

    companion object {
        private const val TYPE_TEXT = 1
        private const val TYPE_TEXT_VIEW = 2

        /** 仅构建 CharSequence，不绑定 TextView。 */
        @JvmStatic
        fun from(context: Context, text: CharSequence): TextLinkBuilder {
            return TextLinkBuilder(TYPE_TEXT)
                .setContext(context)
                .setText(text)
        }

        /** 直接作用于 TextView。 */
        @JvmStatic
        fun on(textView: TextView): TextLinkBuilder {
            return TextLinkBuilder(TYPE_TEXT_VIEW)
                .setContext(textView.context)
                .setTextView(textView)
        }
    }
}
