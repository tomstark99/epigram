package com.epigram.android.ui

import com.epigram.android.data.arch.android.BaseMvp

interface MainActivityMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun load()
    }

    interface Presenter : BaseMvp.Presenter {
        fun onCreate()
    }
}