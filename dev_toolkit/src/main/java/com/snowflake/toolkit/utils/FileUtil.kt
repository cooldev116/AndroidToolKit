package com.snowflake.toolkit.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File

/**
 * @description:文件工具类
 * @author:Melon
 * @date:2026/3/10
 */
object FileUtil {
    /**
     * @description 格式化文件大小
     * @author Melon
     * @time 2026/3/10 11:32
     */
    fun formatFileSize(sizeInBytes: Long): Pair<String, String> {
        if (sizeInBytes <= 0) return Pair("0", "KB")

        val kb = 1024.0
        val mb = kb * 1024
        val gb = mb * 1024

        return when {
            sizeInBytes >= gb -> {
                val value = sizeInBytes / gb
                Pair("%.1f".format(value), "G")
            }

            sizeInBytes >= mb -> {
                val value = sizeInBytes / mb
                Pair("%.1f".format(value), "M")
            }

            sizeInBytes >= kb -> {
                val value = sizeInBytes / kb
                Pair("%.1f".format(value), "KB")
            }

            else -> Pair(sizeInBytes.toString(), "B")
        }
    }

    /**
     * @description 获取文件的MimeType，如image/png,image/png,video/mp4等
     * @author Melon
     * @time 2026/3/26 20:57
     */
    fun getMimeType(file: File): String {
        val extension = file.extension.takeIf { it.isNotEmpty() }?.lowercase()
        return if (extension != null) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                ?: "application/octet-stream"
        } else {
            "application/octet-stream"
        }
    }

    /**
     * @description 文件Uri转路径Path
     * @author Melon
     * @time 2026/4/20 14:42
     */
    fun uriToPath(context: Context, uri: Uri): String? {
        if (uri.scheme == "file") {
            return uri.path
        }
        val resolver = context.contentResolver

        // 尝试从 MediaStore DATA 列直接取绝对路径（部分系统可用）。
        runCatching {
            resolver.query(uri, arrayOf("_data"), null, null, null)?.use { cursor ->
                val index = cursor.getColumnIndex("_data")
                if (index >= 0 && cursor.moveToFirst()) {
                    val value = cursor.getString(index)
                    if (!value.isNullOrBlank()) return value
                }
            }
        }

        // SAF 文档无法直接拿真实路径时，复制到缓存目录，返回可访问的本地路径。
        val displayName = runCatching {
            resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0 && cursor.moveToFirst()) {
                        cursor.getString(index)
                    } else {
                        null
                    }
                }
        }.getOrNull()?.takeIf { it.isNotBlank() } ?: "pick_${System.currentTimeMillis()}"

        val safeName = displayName.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        val targetFile = File(context.cacheDir, safeName)
        return runCatching {
            resolver.openInputStream(uri)?.use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return null
            targetFile.absolutePath
        }.getOrNull()
    }
}