package com.snowflake.toolkit.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.snowflake.toolkit.ext.getViewBinding
import com.snowflake.toolkit.inter.ILoading
import com.snowflake.toolkit.utils.LoadingHandler

/**
 * @description:带有VewBinding的Fragment基类
 * @author:Melon
 * @date:2025/6/12
 */
abstract class BaseVBFragment<VB : ViewBinding> : Fragment(), ILoading {

    private var _binding: VB? = null

    protected val binding: VB get() = _binding!!

    private val loadingHandler by lazy {
        LoadingHandler()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        subscribeData()
        initData()
    }

    protected open fun initView() {}

    protected open fun initListener() {}

    protected open fun initData() {}

    /**
     * @description ViewModel的数据回调
     * @author Melon
     * @time 2025/6/9 17:50
     */
    protected open fun subscribeData() {}

    override fun showLoading(msg: String) {
        activity?.apply {
            loadingHandler.showLoading(this, msg)
        }
    }

    override fun dismissLoading() {
        loadingHandler.dismissLoading()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}