package com.epigram.android.ui

import com.f2prateek.rx.preferences2.Preference
import com.epigram.android.data.arch.android.BasePresenter
import com.epigram.android.data.arch.PreferenceModule

class MainActivityPresenter(mainActivityView: MainActivityMvp.View, private val seenNew: Preference<Boolean> = PreferenceModule.seenNew, private val c: Preference<Int> = PreferenceModule.counter) :
    BasePresenter<MainActivityMvp.View>(mainActivityView),
    MainActivityMvp.Presenter {

    var counter = c.get()

    override fun onCreate() {
        if(counter >= 4) c.set(0) else c.set(counter + 1)
//        val showWelcome = seenNew.get()
        view?.load()
        seenNew.set(true)

//        if(showWelcome) {
//            if(counter < 2) {
//
//            }
//            counter--
//            c.set(counter)
//        }
    }
}