package com.example.dailyfootprint.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GrassDecoration(private val divHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        val count = state.itemCount
        //val offset = 20

        if (position == 0) {
            outRect.top = divHeight
        } else if (position == count-1) {
            outRect.bottom = divHeight
        } else {
            outRect.top = divHeight
            outRect.bottom = divHeight
        }
    }
}