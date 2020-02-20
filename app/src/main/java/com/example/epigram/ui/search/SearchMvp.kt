package com.example.epigram.ui.search

import com.example.epigram.arch.android.BaseMvp

interface SearchMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun setClickables()
    }

    interface Presenter : BaseMvp.Presenter {
        fun onCreate()
    }
}