package com.snowflake.toolkit.ext

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

/**
 * @description:DialogFragment扩展
 * @author:Melon
 * @date:2025/6/27
 */
fun DialogFragment.showExt(activity: FragmentActivity, tag: String? = null) {
    showSafe(activity.supportFragmentManager, tag, activity)
}

// Fragment 扩展
fun DialogFragment.showExt(fragment: Fragment, tag: String? = null) {
    showSafe(fragment.childFragmentManager, tag, fragment.viewLifecycleOwner)
}

private fun DialogFragment.showSafe(
    fragmentManager: FragmentManager,
    tag: String?,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner
) {
    val realTag = if (tag.isNullOrEmpty()) this::class.java.simpleName else tag
    if (!this.isAdded && fragmentManager.findFragmentByTag(realTag) == null) {
        if (!fragmentManager.isStateSaved) {
            // 直接显示
            show(fragmentManager, realTag)
        } else {
            // 延迟到 RESUMED 再显示
            lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    if (!this@showSafe.isAdded) {
                        show(fragmentManager, realTag)
                    }
                }
            }
        }
    }
}