package com.epigram.android.ui.settings

import com.epigram.android.data.arch.android.BaseMvp

interface SettingsMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun setClickables()
        fun load()
    }
    interface Presenter : BaseMvp.Presenter {
        fun onCreate()
        fun setTheme(i: Int)
        fun setLayout(i: Int)
        fun setPersonalisation(i: Int)
        fun reload()
    }
}

