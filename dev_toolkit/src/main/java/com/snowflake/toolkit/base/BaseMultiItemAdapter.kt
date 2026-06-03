package com.snowflake.toolkit.base

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.snowflake.toolkit.entity.MultiItemBean

/**
 * @description:多布局基类
 * @author:Melon
 * @date:2025/8/14
 */
abstract class BaseMultiItemAdapter<T> :
    BaseMultiItemQuickAdapter<MultiItemBean<T>, BaseViewHolder>()