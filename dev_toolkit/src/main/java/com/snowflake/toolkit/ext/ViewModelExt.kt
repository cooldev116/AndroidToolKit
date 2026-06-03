package com.snowflake.toolkit.ext

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @description:ViewModel扩展函数
 * @author:Melon
 * @date:2025/8/11
 */

/**
 * @description 带有异常的启动协程扩展函数
 * @author Melon
 * @time 2025/8/11 1:22
 */
fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    onError: (Throwable) -> Unit = { it.printStackTrace() },
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(context, start = start) {
        try {
            block()
        } catch (e: Throwable) {
            onError(e)
        }
    }
}

/**
 * @description 快速创建
 * @author Melon
 * @time 2025/12/3 15:19
 */
fun <T> ViewModel.liveDataResult(): MutableLiveData<Result<T>> {
    return MutableLiveData<Result<T>>()
}

