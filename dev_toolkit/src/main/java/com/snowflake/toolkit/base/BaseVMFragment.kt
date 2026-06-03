package com.snowflake.toolkit.base

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.snowflake.toolkit.ext.getViewModel

/**
 * @description:带有ViewModel的Fragment基类
 * @author:Melon
 * @date:2025/6/12
 */
abstract class BaseVMFragment<VB : ViewBinding, VM : ViewModel> : BaseVBFragment<VB>() {
    protected open val viewModel: VM by lazy(mode = LazyThreadSafetyMode.NONE) {
        getViewModel()
    }
}