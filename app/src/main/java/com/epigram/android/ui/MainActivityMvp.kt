package com.epigram.android.ui

import com.epigram.android.arch.android.BaseMvp

interface MainActivityMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun load(showWelcome: Boolean)
    }

    interface Presenter : BaseMvp.Presenter {
        fun onCreate()
    }
}