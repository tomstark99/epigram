package com.epigram.android.data.arch.android

interface BaseMvp {
    interface View {
        fun finish()
    }

    interface Presenter {
        fun onDestroy()
    }
}