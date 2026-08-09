package com.snowflake.toolkit.ui.link

import android.widget.TextView

/**
 * @description: TextView 可点击链接扩展，简化 [TextLinkBuilder] 调用。
 * @author: Melon
 * @date: 2026/8/9
 */

/** 将多条链接规则应用到当前 TextView。 */
fun TextView.applyLinks(vararg links: TextLink) {
    if (links.isEmpty()) return
    TextLinkBuilder.on(this)
        .addLinks(links.toList())
        .build()
}

/** 将链接规则列表应用到当前 TextView。 */
fun TextView.applyLinks(links: List<TextLink>) {
    if (links.isEmpty()) return
    TextLinkBuilder.on(this)
        .addLinks(links)
        .build()
}
