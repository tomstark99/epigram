package com.epigram.android.ui

import com.f2prateek.rx.preferences2.Preference
import com.epigram.android.arch.android.BasePresenter
import com.epigram.android.arch.PreferenceModule

class MainActivityPresenter(mainActivityView: MainActivityMvp.View, private val firstUse: Preference<Boolean> = PreferenceModule.isFirstUse, private val c: Preference<Int> = PreferenceModule.counter) :
    BasePresenter<MainActivityMvp.View>(mainActivityView),
    MainActivityMvp.Presenter {

    var counter = c.get()

    override fun onCreate() {
        val showWelcome = firstUse.get()
        view?.load(showWelcome)

        if(showWelcome) {
            if(counter < 2) {
                firstUse.set(false)
            }
            counter--
            c.set(counter)
        }
    }
}