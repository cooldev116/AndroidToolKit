package com.snowflake.toolkit.device

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.github.gzuliyujiang.oaid.DeviceID
import com.github.gzuliyujiang.oaid.IGetter
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 通过 Android_CN_OAID（[DeviceID.getOAID]）异步获取 OAID。
 *
 * 耗时不固定：多数机型约 100ms～2s；失败时可能不回调。
 * 业务侧请用 [DeviceUdidUtil.initOaid] 的超时回调。
 */
internal object DeviceOaidHelper {

    private const val TAG = "DeviceOaidHelper"

    private val started = AtomicBoolean(false)
    private val finished = AtomicBoolean(false)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val waiters = CopyOnWriteArrayList<(String?) -> Unit>()

    @Volatile
    private var resultOaid: String? = null

    /**
     * 拉取 OAID。完成后主线程回调 [onFinished]（成功传 oaid，失败/未拿到传 null）。
     * 已完成后的再次调用会立即回调。
     */
    fun fetch(context: Context, onFinished: (String?) -> Unit) {
        if (finished.get()) {
            mainHandler.post { onFinished(resultOaid) }
            return
        }
        waiters.add(onFinished)
        if (!started.compareAndSet(false, true)) {
            return
        }
        fetchFromCnOaid(context.applicationContext)
    }

    private fun notifySuccess(oaid: String) {
        if (oaid.isBlank()) return
        if (!finished.compareAndSet(false, true)) return
        resultOaid = oaid
        dispatch(oaid)
    }

    private fun dispatch(oaid: String?) {
        val list = waiters.toList()
        waiters.clear()
        mainHandler.post {
            list.forEach { it.invoke(oaid) }
        }
    }

    private fun fetchFromCnOaid(context: Context) {
        try {
            DeviceID.getOAID(context, object : IGetter {
                override fun onOAIDGetComplete(result: String?) {
                    if (!result.isNullOrBlank()) {
                        Log.d(TAG, "CN_OAID success")
                        notifySuccess(result)
                    }
                }

                override fun onOAIDGetError(error: Exception?) {
                    Log.d(TAG, "CN_OAID error: ${error?.message}")
                }
            })
        } catch (t: Throwable) {
            Log.e(TAG, "CN_OAID failed", t)
        }
    }
}
