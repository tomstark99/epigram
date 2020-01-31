package com.example.epigram

import com.f2prateek.rx.preferences2.Preference
import com.example.epigram.arch.android.BasePresenter
import com.example.epigram.arch.PreferenceModule

class MainActivityPresenter(mainActivityView: MainActivityMvp.View, private val firstUse: Preference<Boolean> = PreferenceModule.isFirstUse) :
    BasePresenter<MainActivityMvp.View>(mainActivityView), MainActivityMvp.Presenter {

    override fun onCreate() {
        val showWelcome = firstUse.get()
        view?.load(showWelcome)

        if(showWelcome){
            firstUse.set(false)
        }
    }
}