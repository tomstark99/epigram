package com.epigram.android.ui.search

import com.epigram.android.arch.android.BaseMvp

interface SearchMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun setClickables()
    }

    interface Presenter : BaseMvp.Presenter {
        fun onCreate()
    }
}