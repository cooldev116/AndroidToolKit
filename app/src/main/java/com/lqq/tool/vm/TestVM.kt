package com.lqq.tool.vm

import com.snowflake.toolkit.base.BaseViewModel

/**
 * @description:
 * @author:Melon
 * @date:2025/12/3
 */
class TestVM : BaseViewModel() {
    fun test(){
        loadingStatus.postValue(true)
    }
}