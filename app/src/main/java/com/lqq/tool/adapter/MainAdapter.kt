package com.lqq.tool.adapter

import com.lqq.tool.R
import com.lqq.tool.bean.MainBean
import com.lqq.tool.databinding.ItemTestBinding
import com.snowflake.toolkit.base.BaseVBAdapter

/**
 * @description:测试列表
 * @author:Melon
 * @date:2025/9/13
 */
class MainAdapter : BaseVBAdapter<MainBean, ItemTestBinding>(R.layout.item_test) {
    override fun convert(binding: ItemTestBinding, item: MainBean, position: Int) {
        binding.tvTitle.text = item.title
    }
}