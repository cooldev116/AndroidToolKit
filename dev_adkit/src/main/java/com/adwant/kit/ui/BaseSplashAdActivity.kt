package com.adwant.kit.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import com.adwant.kit.databinding.KitActivitySplashBinding
import com.adwant.kit.inter.ISplashStyle
import com.adwant.kit.ext.showSplashAd
import com.snowflake.toolkit.base.BaseVBMultiActivity

/**
 * @description:开屏广告页面公共基类（样式绑定 + 广告展示流程）
 * @author:Melon
 * @date:2026/4/27
 */
abstract class BaseSplashAdActivity : BaseVBMultiActivity<KitActivitySplashBinding>() {

    private var progressAnimator: ValueAnimator? = null
    private var hasNotifiedCompleted = false

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
     * 展示闪屏广告
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
        showSplashAd(adIds[0]) { _, _ ->
            showSplashAd(adIds[1]) { _, _ ->
                notifySplashCompleted()
            }
        }
    }

    private fun notifySplashCompleted() {
        if (hasNotifiedCompleted) return
        hasNotifiedCompleted = true
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
