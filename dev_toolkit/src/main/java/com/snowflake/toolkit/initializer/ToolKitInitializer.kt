package com.snowflake.toolkit.initializer

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.startup.Initializer
import com.snowflake.toolkit.manger.ToolKitManager
import timber.log.Timber
import java.util.Collections

/**
 * @description:初始化（含 debug 下 Timber 种植）
 * @author:Melon
 * @date:2025/6/27
 */
class ToolKitInitializer : Initializer<ToolKitManager> {
    override fun create(context: Context): ToolKitManager {
        ToolKitManager.instance.init(context)
        plantTimberIfNeeded(context)
        return ToolKitManager.instance
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }

    /**
     * 仅 debug 包种植 [Timber.DebugTree]；若宿主已 plant 过则跳过，避免重复输出。
     * release 默认无 Tree，生产环境不会因 Timber 调用而产生日志。
     */
    private fun plantTimberIfNeeded(context: Context) {
        val debuggable =
            (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (!debuggable) return
        if (Timber.forest().isNotEmpty()) return
        Timber.plant(Timber.DebugTree())
        Timber.d("Timber DebugTree planted by ToolKitInitializer")
    }
}
