package com.snowflake.toolkit.utils

import android.os.Build
import com.snowflake.toolkit.manger.ToolKitManager

/**
 * @description:APP相关工具类
 * @author:Melon
 * @date:2026/3/30
 */
object AppInfoUtil {

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
            packageInfo.versionName
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}