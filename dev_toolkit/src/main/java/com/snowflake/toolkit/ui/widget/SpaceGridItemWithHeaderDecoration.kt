package com.snowflake.toolkit.ui.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.chad.library.adapter.base.BaseQuickAdapter

/**
 * @description:基于BaseQuickAdapter的布局是GridLayoutManager并且带有Header的条目间隔
 * @author:Melon
 * @date:2025/7/22
 */
class SpaceGridItemWithHeaderDecoration(
    private val spanCount: Int,
    private val verticalSpace: Int,
    private val horizontalSpace: Int,
    private val adapter: BaseQuickAdapter<*, *>
) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val headerCount = adapter.headerLayoutCount
        if (position < headerCount) return

        // 真实的数据 position（不包含 header）
        val realPosition = position - headerCount
        val column = realPosition % spanCount

        outRect.left = column * horizontalSpace / spanCount
        outRect.right = horizontalSpace - (column + 1) * horizontalSpace / spanCount

        // 判断是否是第一行（注意是 realPosition）
        if (realPosition < spanCount) {
            outRect.top = verticalSpace
        }

        outRect.bottom = verticalSpace
    }
}