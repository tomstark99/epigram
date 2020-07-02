package com.epigram.android.data.arch.utils

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class SnapHelperOne : LinearSnapHelper() {

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager?,
        velocityX: Int,
        velocityY: Int
    ): Int {
        if(!(layoutManager is RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION
        }

        val currentView: View? = findSnapView(layoutManager)

        if(currentView == null) {
            return RecyclerView.NO_POSITION
        }
        val myLayoutManager: LinearLayoutManager = layoutManager as LinearLayoutManager
        val position1 = myLayoutManager.findFirstVisibleItemPosition()
        val position2 = myLayoutManager.findLastVisibleItemPosition()

        var currentPosition: Int = layoutManager.getPosition(currentView)

        if(velocityX > 800) {
            currentPosition = position2
        } else if (velocityX < 800) {
            currentPosition = position1
        }

        if(currentPosition == RecyclerView.NO_POSITION){
            return RecyclerView.NO_POSITION
        }
        return currentPosition
    }
}