package com.example.epigram.arch.android

interface BaseMvp {
    interface View {
        fun finish()
    }

    interface Presenter {
        fun onDestroy()
    }
}