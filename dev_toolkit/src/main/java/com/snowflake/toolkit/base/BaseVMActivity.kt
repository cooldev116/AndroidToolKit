package com.snowflake.toolkit.base

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.snowflake.toolkit.ext.getViewModel

/**
 * @description:带有ViewModel的Activity基类
 * @author:Melon
 * @date:2025/6/9
 */
abstract class BaseVMActivity<VB : ViewBinding, VM : ViewModel> : BaseVBActivity<VB>() {
    protected open val viewModel: VM by lazy(mode = LazyThreadSafetyMode.NONE) {
        getViewModel()
    }
}