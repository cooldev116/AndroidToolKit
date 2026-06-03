package com.snowflake.toolkit.initializer

import android.content.Context
import androidx.startup.Initializer
import com.snowflake.toolkit.manger.ToolKitManager
import java.util.Collections

/**
 * @description:初始化
 * @author:Melon
 * @date:2025/6/27
 */
class ToolKitInitializer : Initializer<ToolKitManager> {
    override fun create(context: Context): ToolKitManager {
        ToolKitManager.instance.init(context)
        return ToolKitManager.instance
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }
}