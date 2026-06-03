package com.snowflake.toolkit.ui.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @description:RecyclerView九宫格布局条目间距
 * @author:Melon
 * @param spanCount 列数
 * @param space 间距
 * @param includeEdge 是否包含外边距
 * @date:2025/6/24
 */
class SpaceGridItemDecoration(
    private val spanCount: Int,
    private val verticalSpace: Int,
    private val horizontalSpace: Int,
    private val includeEdge: Boolean = false
) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        //当前条目的索引
        val position = parent.getChildAdapterPosition(view)
        //当前条目的列索引
        val column = position % spanCount
        if (includeEdge) {
            //包含边缘
            outRect.left = horizontalSpace - column * horizontalSpace / spanCount
            outRect.right = (column + 1) * horizontalSpace / spanCount

            if (position < spanCount) {
                outRect.top = verticalSpace
            }
            outRect.bottom = verticalSpace
        } else {
            outRect.left = column * horizontalSpace / spanCount
            outRect.right = horizontalSpace - (column + 1) * horizontalSpace / spanCount
            if (position >= spanCount) {
                outRect.top = verticalSpace
            }
        }
    }
}