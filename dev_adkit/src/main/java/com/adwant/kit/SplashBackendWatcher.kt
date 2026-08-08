package com.adwant.kit

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.adwant.kit.ui.BaseSplashAdActivity
import com.adwant.kit.ui.SplashBackendAdActivity
import java.lang.ref.WeakReference

/**
 * 监听应用前后台：退出后台记录时间，回前台且停留时长达到阈值时启动宿主的后台开屏页。
 */
internal class SplashBackendWatcher(
    private val application: Application,
    private val splashActivityClass: Class<out SplashBackendAdActivity>,
    private val thresholdMs: Long
) : DefaultLifecycleObserver, Application.ActivityLifecycleCallbacks {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var backgroundAtMs = 0L
    private var currentActivityRef: WeakReference<Activity>? = null
    private var started = false

    fun start() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post { start() }
            return
        }
        if (started) return
        started = true
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        AdKitLog.i(
            "SplashBackendWatcher started, splash=${splashActivityClass.name}, thresholdMs=$thresholdMs"
        )
    }

    override fun onStop(owner: LifecycleOwner) {
        backgroundAtMs = SystemClock.elapsedRealtime()
        // 退后台视为新一轮前台会话，清空插屏展示次数以便回前台后重新计数
        AdKit.instance.resetShowInterstitialCount()
        AdKitLog.d("app moved to background, backgroundAtMs=$backgroundAtMs")
    }

    override fun onStart(owner: LifecycleOwner) {
        if (backgroundAtMs <= 0L) return
        val elapsed = SystemClock.elapsedRealtime() - backgroundAtMs
        backgroundAtMs = 0L
        AdKitLog.d("app moved to foreground, backgroundElapsedMs=$elapsed")
        if (elapsed < thresholdMs) {
            AdKitLog.d("skip backend splash, elapsed < threshold")
            return
        }
        // 等当前 Activity 生命周期回调跑完，再取前台 Activity 启动开屏
        mainHandler.post { tryLaunchBackendSplash() }
    }

    private fun tryLaunchBackendSplash() {
        if (!AdKit.instance.getIsAllowShowAd()) {
            AdKitLog.i("skip backend splash, isAllowShowAd=false")
            return
        }
        val activity = currentActivityRef?.get()
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            AdKitLog.w("skip backend splash, no valid foreground activity")
            return
        }
        if (activity is BaseSplashAdActivity) {
            AdKitLog.d("skip backend splash, already on splash page")
            return
        }
        AdKitLog.i("launch backend splash: ${splashActivityClass.name}")
        activity.startActivity(Intent(activity, splashActivityClass))
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivityRef?.get() === activity) {
            currentActivityRef = null
        }
    }
}
