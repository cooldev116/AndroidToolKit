package com.snowflake.toolkit.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @description:
 * @author:Melon
 * @date:2025/12/3
 */
open class BaseViewModel : ViewModel() {
    /**
     * 加载框状态
     */
    val loadingStatus = MutableLiveData<Boolean>()
}