package com.snowflake.toolkit.manger

import android.content.Context

/**
 * @description:工具管理类
 * @author:Melon
 * @date:2025/5/30
 */
class ToolKitManager private constructor() {

    private lateinit var context: Context

    companion object {
        val instance by lazy {
            ToolKitManager()
        }
    }

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    fun getContext(): Context {
        return context
    }
}