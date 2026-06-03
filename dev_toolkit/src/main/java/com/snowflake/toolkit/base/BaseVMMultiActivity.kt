package com.snowflake.toolkit.base

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.snowflake.toolkit.ext.getMultiViewModel

/**
 * @description:多重继承
 * @author:Melon
 * @date:2025/7/31
 */
abstract class BaseVMMultiActivity<VB : ViewBinding, VM : ViewModel> : BaseVBMultiActivity<VB>() {
    protected val viewModel: VM by lazy(mode = LazyThreadSafetyMode.NONE) {
        getMultiViewModel()
    }
}