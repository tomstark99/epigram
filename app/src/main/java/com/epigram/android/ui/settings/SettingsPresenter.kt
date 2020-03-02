package com.epigram.android.ui.settings

import com.epigram.android.arch.PreferenceModule
import com.epigram.android.arch.android.BaseMvp
import com.epigram.android.arch.android.BasePresenter
import com.f2prateek.rx.preferences2.Preference

class SettingsPresenter (view : SettingsMvp.View, val themeId: Preference<Int> = PreferenceModule.darkMode) : BasePresenter<SettingsMvp.View>(view), SettingsMvp.Presenter {
    override fun onCreate() {
        view?.load()
        view?.setClickables()
    }

    override fun setTheme(i: Int) {
        themeId.set(i)
        view?.load()
    }
}