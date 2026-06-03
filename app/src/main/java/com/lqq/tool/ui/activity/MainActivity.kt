package com.lqq.tool.ui.activity

import com.lqq.tool.adapter.MainAdapter
import com.lqq.tool.bean.MainBean
import com.lqq.tool.databinding.ActivityMainBinding
import com.snowflake.toolkit.base.BaseVBActivity
import com.snowflake.toolkit.ext.dp2Px
import com.snowflake.toolkit.ext.openActivity
import com.snowflake.toolkit.ui.widget.SpaceItemDecoration

class MainActivity : BaseVBActivity<ActivityMainBinding>() {

    private val list = mutableListOf(MainBean("广告测试", AdTestActivity::class.java))
    private val listAdapter = MainAdapter()

    override fun initView() {
        super.initView()

        binding.rvFun.apply {
            addItemDecoration(SpaceItemDecoration(10f.dp2Px()))
            adapter = listAdapter
        }
        listAdapter.setNewInstance(list)
    }

    override fun initListener() {
        super.initListener()

        listAdapter.setOnItemClickListener { _, _, position ->
            openActivity(listAdapter.data[position].clazz)
        }
    }
}