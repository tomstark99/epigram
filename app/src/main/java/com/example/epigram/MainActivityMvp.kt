package com.example.epigram

import com.example.epigram.arch.android.BaseMvp

interface MainActivityMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun load(showWelcome: Boolean)
    }

    interface Presenter : BaseMvp.Presenter {
        fun onCreate()
    }
}