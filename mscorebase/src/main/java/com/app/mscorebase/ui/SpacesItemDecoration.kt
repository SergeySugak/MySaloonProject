package com.app.mscorebase.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SpacesItemDecoration(
    private val margin: Int,
    private val colCnt: Int,
    private val rowCnt: Int
) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)
        val row = position / colCnt
        val col = position % colCnt

        outRect.left = margin - col * margin / colCnt
        outRect.right = (col + 1) * margin / colCnt

        when (row) {
            0 -> {
                outRect.top = margin
                outRect.bottom = margin / 2
            }
            rowCnt - 1 -> {
                outRect.top = margin / 2
                outRect.bottom = margin
            }
            else -> {
                outRect.top = margin / 2
                outRect.bottom = margin / 2
            }
        }
    }
}