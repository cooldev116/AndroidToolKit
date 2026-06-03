package com.snowflake.toolkit.entity

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * @description:多类型条目
 * @author:Melon
 * @date:2025/8/14
 */
data class MultiItemBean<T>(val type: Int, val dataBean: T? = null) : MultiItemEntity {
    override val itemType: Int
        get() = type
}
