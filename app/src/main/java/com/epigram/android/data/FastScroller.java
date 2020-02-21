package com.epigram.android.data;

import android.content.Context;
import android.util.DisplayMetrics;
import androidx.recyclerview.widget.LinearSmoothScroller;

public class FastScroller extends LinearSmoothScroller {

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return super.calculateSpeedPerPixel(displayMetrics)*0.1f;
    }

    public FastScroller(Context context) {
        super(context);
    }
}


