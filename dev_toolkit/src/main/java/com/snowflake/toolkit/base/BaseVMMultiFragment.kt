package com.snowflake.toolkit.base

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.snowflake.toolkit.ext.getMultiViewModel

/**
 * @description:
 * @author:Melon
 * @date:2025/7/25
 */
abstract class BaseVMMultiFragment<VB : ViewBinding, VM : ViewModel> : BaseVBMultiFragment<VB>() {
    protected open val viewModel: VM by lazy(mode = LazyThreadSafetyMode.NONE) {
        getMultiViewModel()
    }
}