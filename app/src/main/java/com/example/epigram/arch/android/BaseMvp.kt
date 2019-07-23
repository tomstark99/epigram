package com.example.epigram.arch.android

interface BaseMvp {
    interface View {}
    interface Presenter {
        fun onDestroy()
    }
}