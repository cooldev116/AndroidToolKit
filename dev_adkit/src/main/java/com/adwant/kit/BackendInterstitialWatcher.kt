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
 * 在前台 Activity 上按 [adIds] 顺序依次展示插屏。
 *
 * 与后台开屏并存时：等 [SplashBackendAdActivity] finish 后再展示，避免叠弹。
 */
internal class BackendInterstitialWatcher(
    private val application: Application,
    private val adIds: List<String>,
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
            "BackendInterstitialWatcher started, adIds=$adIds, thresholdMs=$thresholdMs"
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
        // 再 post 一帧，保证同一次回前台时 SplashBackendWatcher 先标记 / 拉起开屏
        mainHandler.post {
            mainHandler.post { tryShowBackendInterstitial() }
        }
    }

    /**
     * 回前台后尝试展示后台插屏。
     * 若本轮已拉起后台开屏，则挂起至开屏 finish；展示时重新取当前前台页。
     */
    private fun tryShowBackendInterstitial() {
        if (!AdKit.instance.getIsAllowShowAd()) {
            AdKitLog.i("skip backend interstitial, isAllowShowAd=false")
            return
        }
        AdKit.instance.runAfterBackendSplashOrNow {
            showBackendInterstitialOnCurrentActivity()
        }
    }

    /**
     * 在当前前台 [FragmentActivity] 上展示后台插屏；开屏页或无效页则跳过。
     */
    private fun showBackendInterstitialOnCurrentActivity() {
        val activity = currentActivityRef?.get()
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            AdKitLog.w("skip backend interstitial, no valid foreground activity")
            return
        }
        // 仍停在开屏页时不再弹（理论上 finish 后不应走到这里）
        if (activity is SplashBackendAdActivity) {
            AdKitLog.d("skip backend interstitial, still on SplashBackendAdActivity")
            return
        }
        val fragmentActivity = activity as? FragmentActivity
        if (fragmentActivity == null) {
            AdKitLog.w("skip backend interstitial, foreground activity is not FragmentActivity")
            return
        }
        AdKitLog.i("show backend interstitial: adIds=$adIds")
        fragmentActivity.showBackendInterstitialAd(adIds)
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
