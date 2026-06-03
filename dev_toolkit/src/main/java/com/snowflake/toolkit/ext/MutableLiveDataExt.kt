package com.snowflake.toolkit.ext

import androidx.lifecycle.MutableLiveData
import com.snowflake.toolkit.exception.ApiException

/**
 * @description:
 * @author:Melon
 * @date:2025/11/12
 */
/**
 * 成功
 */
fun <T> MutableLiveData<Result<T>>.postSuccess(data: T) = postValue(Result.success(data))

/**
 * 业务失败
 */
fun <T> MutableLiveData<Result<T>>.postFailure(msg: String?, code: Int = -1) =
    postValue(Result.failure(ApiException(code, msg)))

/**
 * 非业务失败
 */
fun <T> MutableLiveData<Result<T>>.postError(e: Throwable) = postValue(Result.failure(e))