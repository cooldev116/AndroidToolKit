package com.snowflake.toolkit.ui.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @description:RecyclerView列表条目的间距
 * @author:Melon
 * @param space 间距
 * @param orientation 方向，默认是竖直方向
 * @date:2025/6/13
 */
class SpaceItemDecoration(
    private val space: Int,
    private val orientation: Int = LinearLayoutManager.VERTICAL
) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position != 0) {
            if (orientation == LinearLayoutManager.VERTICAL) {
                outRect.top = space
            } else {
                outRect.left = space
            }
        }
    }
}