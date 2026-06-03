package com.snowflake.toolkit.exception

/**
 * @description:业务异常
 * @author:Melon
 * @date:2025/11/12
 */
data class ApiException(val code: Int, val msg: String?) : Exception(msg)
