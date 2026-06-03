package com.snowflake.toolkit.utils

import android.content.Context
import android.text.format.Formatter
import java.io.File

/**
 * @description:缓存工具
 * @author:Melon
 * @date:2025/9/15
 */
object CacheUtil {
    /**
     * 获取应用缓存大小（格式化后的字符串）
     */
    fun getTotalCacheSize(context: Context): String {
        var cacheSize: Long = getFolderSize(context.cacheDir)
        context.externalCacheDir?.let {
            cacheSize += getFolderSize(it)
        }
        return Formatter.formatFileSize(context, cacheSize)
    }

    /**
     * 清理应用缓存
     */
    fun clearAllCache(context: Context) {
        deleteDir(context.cacheDir)
        context.externalCacheDir?.let {
            deleteDir(it)
        }
    }

    /**
     * 递归计算文件夹大小
     */
    private fun getFolderSize(file: File?): Long {
        var size: Long = 0
        if (file != null && file.exists()) {
            file.listFiles()?.forEach {
                size += if (it.isDirectory) {
                    getFolderSize(it)
                } else {
                    it.length()
                }
            }
        }
        return size
    }

    /**
     * 递归删除文件夹
     */
    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            dir.listFiles()?.forEach {
                deleteDir(it)
            }
        }
        return dir?.delete() ?: false
    }
}