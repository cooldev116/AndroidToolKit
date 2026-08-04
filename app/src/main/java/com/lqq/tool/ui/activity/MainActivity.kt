package com.lqq.tool.ui.activity

import android.util.Log
import com.lqq.tool.adapter.MainAdapter
import com.lqq.tool.bean.MainBean
import com.lqq.tool.databinding.ActivityMainBinding
import com.snowflake.toolkit.base.BaseVBActivity
import com.snowflake.toolkit.device.DeviceUdidUtil
import com.snowflake.toolkit.ext.dp2Px
import com.snowflake.toolkit.ext.openActivity
import com.snowflake.toolkit.ui.widget.SpaceItemDecoration

class MainActivity : BaseVBActivity<ActivityMainBinding>() {

    private val list = mutableListOf(MainBean("广告测试", AdTestActivity::class.java))
    private val listAdapter = MainAdapter()

    override fun initView() {
        super.initView()
        val udid = DeviceUdidUtil.getMd5Udid()
//        815b5fe6be59301a3f868119a9422ba0
        Log.i("TAG", "initView: --------------------------------->>>>>>>>>>>>>>>>>>>>>>>$udid")

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