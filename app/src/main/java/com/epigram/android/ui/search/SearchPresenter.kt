package com.epigram.android.ui.search

import com.epigram.android.arch.android.BasePresenter

class SearchPresenter (searchView: SearchMvp.View) : BasePresenter<SearchMvp.View>(searchView), SearchMvp.Presenter {

    override fun onCreate() {
        view?.setClickables()
    }
}