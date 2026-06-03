package com.adwant.kit

import android.util.Log

/**
 * Unified log utility for AdKit.
 * Logs are printed only when AdKit debug mode is enabled.
 */
internal object AdKitLog {

    private const val BASE_TAG = "AdKit"

    private fun canLog(): Boolean = AdKit.instance.getDebug()

    fun d(message: String) {
        if (!canLog()) return
        Log.d(BASE_TAG, message)
    }

    fun i(message: String) {
        if (!canLog()) return
        Log.i(BASE_TAG, message)
    }

    fun w(message: String) {
        if (!canLog()) return
        Log.w(BASE_TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (!canLog()) return
        Log.e(BASE_TAG, message, throwable)
    }
}
