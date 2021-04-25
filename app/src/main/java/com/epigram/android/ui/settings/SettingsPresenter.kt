package com.epigram.android.ui.settings

import com.epigram.android.data.arch.PreferenceModule
import com.epigram.android.data.arch.android.BasePresenter
import com.f2prateek.rx.preferences2.Preference

class SettingsPresenter (view : SettingsMvp.View,
                         val themeId: Preference<Int> = PreferenceModule.darkMode,
                         val layoutId: Preference<Int> = PreferenceModule.layoutMode,
                         val personalisationId: Preference<Int> = PreferenceModule.advancedForYou) : BasePresenter<SettingsMvp.View>(view), SettingsMvp.Presenter {

    override fun onCreate() {
        view?.load()
        view?.setClickables()
    }

    override fun setTheme(i: Int) {
        themeId.set(i)
    }

    override fun setLayout(i: Int) {
        layoutId.set(i)
    }

    override fun setPersonalisation(i: Int) {
        personalisationId.set(i)
    }

    override fun reload() {
        view?.load()
    }
}