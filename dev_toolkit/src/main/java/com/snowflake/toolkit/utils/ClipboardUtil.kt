package com.snowflake.toolkit.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.snowflake.toolkit.ext.toast
import com.snowflake.toolkit.manger.ToolKitManager

/**
 * @description:剪切板
 * @author:Melon
 * @date:2025/8/7
 */
object ClipboardUtil {
    private val clipboard by lazy {
        ToolKitManager.instance.getContext().run {
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        }
    }

    /**
     * @description 复制文本
     * @param content 要复制的文本内容
     * @param label 一个标记字符串，可以用来描述这段文本
     * @author Melon
     * @time 2025/8/7 17:59
     */
    fun copyToClipboard(content: String, label: String = "复制文本") {
        val clip = ClipData.newPlainText(label, content)
        clipboard.setPrimaryClip(clip)
        "复制成功".toast()
    }
}