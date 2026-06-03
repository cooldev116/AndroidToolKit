package com.snowflake.toolkit.base

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snowflake.toolkit.databinding.ToolkitFragmentRefreshBinding
import com.snowflake.toolkit.entity.MultiItemBean
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @description:支持刷新并支持列表多个条目的Fragment
 * @author:Melon
 * @date:2025/8/14
 */
abstract class BaseRefreshVBMultiItemFragment<T, Adapter : BaseMultiItemAdapter<T>> :
    BaseVBMultiFragment<ToolkitFragmentRefreshBinding>(),
    OnRefreshLoadMoreListener {
    protected val listAdapter by lazy {
        getVBAdapter()
    }

    /**
     * 是否开启下拉刷新
     */
    protected open val isEnableRefresh = true

    /**
     * 是否开启上拉加载更多
     */
    protected open val isEnableLoadMore = true

    /**
     * 当前请求页码
     */
    protected var page = 0

    override fun initView() {
        super.initView()
        initListView()
        initRefresh()
    }

    override fun initData() {
        super.initData()
        loadData()
    }

    /**
     * 初始化刷新控件
     */
    private fun initRefresh() {
        binding.refresh.apply {
            setEnableRefresh(isEnableRefresh)
            setEnableLoadMore(isEnableLoadMore)
        }
    }

    /**
     * @description 初始化列表
     * @author Melon
     * @time 2025/7/24 10:51
     */
    private fun initListView() {
        binding.rvList.apply {
            layoutManager = this@BaseRefreshVBMultiItemFragment.getLayoutManager()
            getItemDecoration()?.let {
                addItemDecoration(it)
            }
            adapter = listAdapter
        }
    }

    override fun initListener() {
        super.initListener()

        binding.refresh.setOnRefreshLoadMoreListener(this)
    }

    /**
     * @description 获取列表适配器
     * @author Melon
     * @time 2025/7/24 10:44
     */
    protected abstract fun getVBAdapter(): Adapter


    /**
     * @description 获取布局方式，子类可以重写
     * @author Melon
     * @time 2025/7/24 10:41
     */
    protected open fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireContext())
    }

    /**
     * @description 条目间隔
     * @author Melon
     * @time 2025/7/24 10:47
     */
    protected open fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return null
    }

    /**
     * @description 刷新
     * @author Melon
     * @time 2025/9/6 19:49
     */
    protected fun autoRefresh() {
        binding.refresh.autoRefresh()
    }

    override fun onRefresh(p0: RefreshLayout) {
        page = 0
        loadData()
    }

    override fun onLoadMore(p0: RefreshLayout) {
        page++
        loadData()
    }

    /**
     * @description 获取数据
     * @author Melon
     * @time 2025/7/24 11:15
     */
    protected abstract fun loadData()

    /**
     * @description 刷新完成
     * @author Melon
     * @time 2025/7/24 11:37
     */
    protected fun finishRefresh() {
        if (binding.refresh.isRefreshing) {
            binding.refresh.finishRefresh()
        }
    }

    /**
     * @description 加载更多完成
     * @author Melon
     * @time 2025/7/24 11:36
     */
    protected fun finishLoadMore() {
        if (binding.refresh.isLoading) {
            binding.refresh.finishLoadMore()
        }
    }

    /**
     * @description 设置数据
     * @author Melon
     * @time 2025/7/24 11:33
     */
    protected fun submitList(dataList: MutableList<MultiItemBean<T>>) {
        finishRefresh()
        finishLoadMore()
        if (page == 0) {
            listAdapter.setNewInstance(mutableListOf())
            listAdapter.setNewInstance(dataList)
        } else {
            if (dataList.isNotEmpty()) {
                listAdapter.addData(dataList)
            } else {
                binding.refresh.finishLoadMoreWithNoMoreData()
            }
        }
    }
}