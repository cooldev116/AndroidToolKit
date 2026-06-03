package com.snowflake.toolkit.inter

/**
 * @description:加载框
 * @author:Melon
 * @date:2025/12/3
 */
interface ILoading {
    /**
     * 显示加载框
     */
    fun showLoading(msg: String = "加载中")

    /**
     * 隐藏加载框
     */
    fun dismissLoading()
}