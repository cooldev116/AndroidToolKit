package com.snowflake.toolkit.base

import androidx.annotation.LayoutRes
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.snowflake.toolkit.entity.MultiItemBean

/**
 * @description:多布局带有广告基类
 * @author:Melon
 * @date:2025/8/14
 */
abstract class BaseMultiItemWithAdAdapter<T> :
    BaseMultiItemQuickAdapter<MultiItemBean<T>, BaseViewHolder>() {

    init {
        addItemType(0, getNativeAdLayoutId())
    }

    /**
     * @description 获取信息流广告id
     * @author Melon
     * @time 2025/8/14 14:34
     */
    abstract fun getNativeAdId(): String

    @LayoutRes
    abstract fun getNativeAdLayoutId(): Int
}