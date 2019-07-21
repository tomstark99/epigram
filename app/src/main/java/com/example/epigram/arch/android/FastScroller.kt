package com.example.epigram.arch.android

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller

class FastScroller(context: Context) : LinearSmoothScroller(context) {

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return super.calculateSpeedPerPixel(displayMetrics) * 0.1f
    }
}


