package com.snowflake.toolkit.ext

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * @description:
 * @author:Melon
 * @date:2025/7/23
 */

/**
 * @description TabLayout和ViewPager2绑定
 * @param vp 要绑定的ViewPager2
 * @param bindingInflater 自定义Tab条目是的布局ViewBinding
 * @param onInitTab 初始化tab的内容
 * @param onTabStyle 切换Tab时，自定义Tab样式需要改在这里处理
 * @author Melon
 * @time 2025/7/23 18:00
 */
fun <VB : ViewBinding> TabLayout.bindVp2(
    vp: ViewPager2,
    bindingInflater: (LayoutInflater) -> VB,
    onInitTab: (VB, Int) -> Unit,
    onTabStyle: ((VB, Boolean) -> Unit)? = null,
    onTabSelectedAction: ((TabLayout.Tab?) -> Unit)? = null,
    onTabUnSelectedAction: ((TabLayout.Tab?) -> Unit)? = null,
    onTabReSelectedAction: ((TabLayout.Tab?) -> Unit)? = null
) {
    //防止重复添加监听
    clearOnTabSelectedListeners()

    val bindingMap = mutableMapOf<TabLayout.Tab, VB>()

    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            onTabSelectedAction?.invoke(tab)
            tab?.let { it ->
                bindingMap[it]?.let {
                    onTabStyle?.invoke(it, true)
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            onTabUnSelectedAction?.invoke(tab)
            tab?.let { it ->
                bindingMap[it]?.let {
                    onTabStyle?.invoke(it, false)
                }
            }
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            onTabReSelectedAction?.invoke(tab)
        }
    })

    TabLayoutMediator(
        this, vp
    ) { tab, position ->
        val binding = bindingInflater(LayoutInflater.from(context))
        tab.customView = binding.root
        onInitTab.invoke(binding, position)
        bindingMap[tab] = binding
        onTabStyle?.invoke(binding, position == 0)
    }.attach()
}