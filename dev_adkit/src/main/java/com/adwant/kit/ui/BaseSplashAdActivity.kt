package com.adwant.kit.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import com.adwant.kit.AbsAdFlowCallback
import com.adwant.kit.AdKit
import com.adwant.kit.AdType
import com.adwant.kit.ad.SplashAd
import com.adwant.kit.databinding.KitActivitySplashBinding
import com.adwant.kit.ext.showSplashAd
import com.adwant.kit.inter.ISplashStyle
import com.snowflake.toolkit.base.BaseVBMultiActivity

/**
 * @description:开屏广告页面公共基类（样式绑定 + 广告展示流程）
 * @author:Melon
 * @date:2026/4/27
 */
abstract class BaseSplashAdActivity : BaseVBMultiActivity<KitActivitySplashBinding>() {

    private var progressAnimator: ValueAnimator? = null
    private var hasNotifiedCompleted = false
    /** 双开屏时预加载的第二开屏句柄，Activity 销毁时需释放 */
    private var pendingSecondSplash: SplashAd? = null

    override fun initView() {
        super.initView()
        binding.bindSplashStyle(this, getSplashStyle())
    }

    /**
     * 假进度：等待广告加载与其它初始化时缓慢推进到 90%
     * 冷启动需在用户同意隐私协议后再调用
     */
    protected fun startFakeProgress() {
        binding.pbSplash.max = PROGRESS_MAX
        binding.pbSplash.progress = 0
        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofInt(0, PROGRESS_HOLD).apply {
            duration = FAKE_PROGRESS_DURATION
            interpolator = DecelerateInterpolator()
            addUpdateListener { animator ->
                binding.pbSplash.progress = animator.animatedValue as Int
            }
            start()
        }
    }

    /**
     * 快速补满进度后再回调完成
     */
    private fun finishWithProgress(done: () -> Unit) {
        progressAnimator?.cancel()
        val current = binding.pbSplash.progress.coerceAtLeast(0)
        if (current >= PROGRESS_MAX) {
            done()
            return
        }
        progressAnimator = ValueAnimator.ofInt(current, PROGRESS_MAX).apply {
            duration = COMPLETE_PROGRESS_DURATION
            addUpdateListener { animator ->
                binding.pbSplash.progress = animator.animatedValue as Int
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.pbSplash.progress = PROGRESS_MAX
                    done()
                }
            })
            start()
        }
    }

    /**
     * 展示闪屏广告。双开屏时：第一开屏 onLoad 预加载第二开屏，onClose 后立即展示。
     */
    protected fun startShowSplash() {
        val adIds = getAdIds()
        if (adIds.isEmpty()) {
            notifySplashCompleted()
            return
        }
        if (adIds.size == 1) {
            showSplashAd(adIds[0]) { _, _ ->
                notifySplashCompleted()
            }
            return
        }
        startDoubleSplash(adIds[0], adIds[1])
    }

    /**
     * 双开屏时序：
     * 1. 请求并展示第一开屏
     * 2. 第一开屏 onLoadSuccess 时预加载第二开屏（不展示）
     * 3. 第一开屏 onClose 时直接展示已预加载的第二开屏
     * 4. 若第一开屏未走到 onLoad（失败/跳过），则退化为关闭后再请求第二开屏
     */
    private fun startDoubleSplash(firstId: String, secondId: String) {
        var firstClosed = false
        var secondLoadFailed = false

        // 第二开屏：预加载失败时不立刻结束（第一开屏可能仍在展示）
        val secondFlowCallback = object : AbsAdFlowCallback() {
            override fun onLoadFail(type: AdType, adId: String, code: Int?, msg: String?) {
                secondLoadFailed = true
                // 第一开屏已关且正在等待第二开屏时，失败则结束整段开屏
                if (firstClosed) {
                    notifySplashCompleted()
                }
            }

            override fun onRenderFail(type: AdType, adId: String, code: Int?, msg: String?) {
                notifySplashCompleted()
            }

            override fun onVideoError(type: AdType, adId: String) {}

            override fun onClose(type: AdType, adId: String) {
                notifySplashCompleted()
            }
        }

        showSplashAd(
            firstId,
            object : AbsAdFlowCallback() {
                override fun onLoadSuccess(type: AdType, adId: String) {
                    // 第一开屏加载成功即预加载第二开屏，缩短两开屏衔接空窗
                    pendingSecondSplash?.destroy()
                    pendingSecondSplash = AdKit.instance.preloadSplashAd(
                        this@BaseSplashAdActivity,
                        secondId,
                        secondFlowCallback
                    )
                    if (pendingSecondSplash == null) {
                        // 不允许展示等：checkAllowShowAd 已回调 onLoadFail
                        secondLoadFailed = true
                    }
                }

                override fun onLoadFail(type: AdType, adId: String, code: Int?, msg: String?) {}

                override fun onRenderFail(type: AdType, adId: String, code: Int?, msg: String?) {}

                override fun onVideoError(type: AdType, adId: String) {}

                override fun onClose(type: AdType, adId: String) {}
            }
        ) { _, _ ->
            firstClosed = true
            val preloaded = pendingSecondSplash
            when {
                // 预加载已失败：跳过第二开屏
                secondLoadFailed || preloaded?.isLoadFailed() == true -> {
                    pendingSecondSplash?.destroy()
                    pendingSecondSplash = null
                    notifySplashCompleted()
                }
                // 已预加载（或仍在加载）：关闭后立刻展示 / 等加载完再展
                preloaded != null -> {
                    preloaded.showPreloaded(this)
                }
                // 第一开屏未 onLoad（失败/跳过）：保持旧行为，关闭后再请求第二开屏
                else -> {
                    showSplashAd(secondId) { _, _ ->
                        notifySplashCompleted()
                    }
                }
            }
        }
    }

    private fun notifySplashCompleted() {
        if (hasNotifiedCompleted) return
        hasNotifiedCompleted = true
        // 未展示的预加载广告在流程结束时释放，已展示的由关闭回调内 destroy
        pendingSecondSplash?.destroy()
        pendingSecondSplash = null
        finishWithProgress {
            onSplashCompleted()
        }
    }

    /**
     * 获取闪屏页样式配置
     */
    protected abstract fun getSplashStyle(): ISplashStyle

    /**
     * 获取广告id
     */
    protected abstract fun getAdIds(): List<String>

    /**
     * 开屏广告展示完成（包括开关、黑名单等未展示的也会调用此方法）
     */
    protected open fun onSplashCompleted() {}

    override fun onDestroy() {
        pendingSecondSplash?.destroy()
        pendingSecondSplash = null
        progressAnimator?.removeAllListeners()
        progressAnimator?.removeAllUpdateListeners()
        progressAnimator?.cancel()
        progressAnimator = null
        super.onDestroy()
    }

    companion object {
        private const val PROGRESS_MAX = 1000
        private const val PROGRESS_HOLD = 900
        private const val FAKE_PROGRESS_DURATION = 6000L
        private const val COMPLETE_PROGRESS_DURATION = 300L
    }
}
