package com.adwant.kit

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.adwant.kit.ext.showBackendInterstitialAd
import com.adwant.kit.ui.SplashBackendAdActivity
import java.lang.ref.WeakReference

/**
 * 监听应用前后台：退出后台停留时长超过 [thresholdMs]（默认 5 秒）后再回前台时，
 * 若当前页不是 [SplashBackendAdActivity]，则在前台 Activity 上展示后台插屏。
 */
internal class BackendInterstitialWatcher(
    private val application: Application,
    private val firstId: String,
    private val secondId: String,
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
            "BackendInterstitialWatcher started, firstId=$firstId, secondId=$secondId, thresholdMs=$thresholdMs"
        )
    }

    override fun onStop(owner: LifecycleOwner) {
        backgroundAtMs = SystemClock.elapsedRealtime()
        // 与后台开屏一致：退后台清空插屏次数，便于新一轮前台会话重新累计
        AdKit.instance.resetShowInterstitialCount()
        AdKitLog.d("app moved to background (interstitial), backgroundAtMs=$backgroundAtMs")
    }

    override fun onStart(owner: LifecycleOwner) {
        if (backgroundAtMs <= 0L) return
        val elapsed = SystemClock.elapsedRealtime() - backgroundAtMs
        backgroundAtMs = 0L
        AdKitLog.d("app moved to foreground (interstitial), backgroundElapsedMs=$elapsed")
        // 后台停留未超过阈值（默认 5 秒）不展示
        if (elapsed <= thresholdMs) {
            AdKitLog.d(
                "skip backend interstitial, elapsed=${elapsed}ms <= threshold=${thresholdMs}ms"
            )
            return
        }
        // 等当前 Activity 生命周期回调跑完，再取前台页判断是否可弹插屏
        mainHandler.post { tryShowBackendInterstitial() }
    }

    /**
     * 回前台后尝试展示后台插屏；已在后台开屏页或 Activity 无效时跳过。
     */
    private fun tryShowBackendInterstitial() {
        if (!AdKit.instance.getIsAllowShowAd()) {
            AdKitLog.i("skip backend interstitial, isAllowShowAd=false")
            return
        }
        val activity = currentActivityRef?.get()
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            AdKitLog.w("skip backend interstitial, no valid foreground activity")
            return
        }
        if (activity is SplashBackendAdActivity) {
            AdKitLog.d("skip backend interstitial, already on SplashBackendAdActivity")
            return
        }
        val fragmentActivity = activity as? FragmentActivity
        if (fragmentActivity == null) {
            AdKitLog.w("skip backend interstitial, foreground activity is not FragmentActivity")
            return
        }
        AdKitLog.i("show backend interstitial: firstId=$firstId, secondId=$secondId")
        fragmentActivity.showBackendInterstitialAd(firstId, secondId)
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
