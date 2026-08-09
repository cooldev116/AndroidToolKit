package com.snowflake.toolkit.utils

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.snowflake.toolkit.manger.ToolKitManager

/**
 * @description:APP相关工具类
 * @author:Melon
 * @date:2026/3/30
 */
object AppInfoUtil {

    /** 宿主清单中渠道 meta-data 的 name，需与 manifestPlaceholders 对应 */
    const val META_CHANNEL = "CHANNEL"

    private val content by lazy {
        ToolKitManager.instance.getContext()
    }

    /**
     * @description 获取应用名称
     * @author Melon
     * @time 2026/3/30 14:21
     */
    fun getAppName(): String {
        return try {
            val pm = content.packageManager
            val appInfo = content.applicationInfo
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * @description 获取应用包名
     * @author Melon
     * @time 2026/8/9 11:50
     */
    fun getPackageName(): String {
        return content.packageName
    }

    /**
     * @description 获取App版本号
     * @author Melon
     * @time 2026/3/30 14:26
     */
    fun getVersionCode(): Long {
        return try {
            val pm = content.packageManager
            val packageInfo = pm.getPackageInfo(content.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    /**
     * @description 获取应用版本名
     * @author Melon
     * @time 2026/3/30 14:27
     */
    fun getVersionName(): String {
        return try {
            val pm = content.packageManager
            val packageInfo = pm.getPackageInfo(content.packageName, 0)
            packageInfo.versionName ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 读取宿主 Application 节点下的渠道号（meta-data name=[META_CHANNEL]）。
     * 未配置时返回空串，避免请求头出现 null。
     */
    fun getChannel(): String {
        return getMetaDataString(META_CHANNEL).orEmpty()
    }

    /**
     * 读取 Application 节点 meta-data 字符串；不存在或非字符串时返回 null。
     * 兼容 Placeholder 注入的 value，以及部分场景写入的 int（会转成字符串）。
     */
    fun getMetaDataString(key: String): String? {
        if (key.isBlank()) return null
        return try {
            val meta = getMetaDataBundle() ?: return null
            when (val value = meta.get(key)) {
                null -> null
                is String -> value.takeIf { it.isNotBlank() }
                else -> value.toString().takeIf { it.isNotBlank() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getMetaDataBundle(): Bundle? {
        val appInfo = content.packageManager.getApplicationInfo(
            content.packageName,
            PackageManager.GET_META_DATA
        )
        return appInfo.metaData
    }
}