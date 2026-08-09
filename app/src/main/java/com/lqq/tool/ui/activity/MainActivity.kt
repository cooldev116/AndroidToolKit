package com.lqq.tool.ui.activity

import android.util.Log
import android.widget.Toast
import com.lqq.tool.adapter.MainAdapter
import com.lqq.tool.bean.MainBean
import com.lqq.tool.databinding.ActivityMainBinding
import com.lqq.tool.vm.TestVM
import com.snowflake.toolkit.base.BaseVMActivity
import com.snowflake.toolkit.device.DeviceUdidUtil
import com.snowflake.toolkit.ext.dp2Px
import com.snowflake.toolkit.ext.openActivity
import com.snowflake.toolkit.ui.widget.SpaceItemDecoration

/**
 * 功能入口页；进入时拉取玩 Android Banner，用于验证网络框架。
 *
 * @author Melon
 * @date 2026/8/9
 */
class MainActivity : BaseVMActivity<ActivityMainBinding, TestVM>() {

    companion object {
        private const val TAG = "NetTest"
    }

    private val list = mutableListOf(MainBean("广告测试", AdTestActivity::class.java))
    private val listAdapter = MainAdapter()

    override fun initView() {
        super.initView()
        val udid = DeviceUdidUtil.getMd5Udid()
        Log.i(TAG, "udid=$udid")

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

    override fun initData() {
        super.initData()
        viewModel.fetchBanner()
    }

    override fun subscribeData() {
        super.subscribeData()
        viewModel.loadingStatus.observe(this) { show ->
            if (show == true) showLoading("加载中...") else dismissLoading()
        }
        // 观察 Banner 结果：成功打日志并 Toast 条数，失败提示原因
        viewModel.bannerResult.observe(this) { result ->
            result.onSuccess { banners ->
                Log.i(TAG, "banner success, size=${banners.size}")
                banners.forEach { Log.i(TAG, "banner -> $it") }
                Toast.makeText(this, "Banner 成功：${banners.size} 条", Toast.LENGTH_SHORT).show()
            }.onFailure { error ->
                Log.e(TAG, "banner failed: ${error.message}", error)
                Toast.makeText(this, "Banner 失败：${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
