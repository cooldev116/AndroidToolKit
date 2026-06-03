package com.snowflake.toolkit.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @description:
 * @author:Melon
 * @date:2025/7/24
 */
class BaseFragmentPagerAdapter : FragmentStateAdapter {

    private val fragments = mutableListOf<Fragment>()

    constructor(
        fragments: MutableList<out Fragment>,
        activity: FragmentActivity
    ) : super(activity) {
        this.fragments.clear()
        this.fragments.addAll(fragments)
    }

    constructor(fragments: MutableList<out Fragment>, fragment: Fragment) : super(fragment) {
        this.fragments.clear()
        this.fragments.addAll(fragments)
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}