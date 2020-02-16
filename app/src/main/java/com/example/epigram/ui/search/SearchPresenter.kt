package com.example.epigram.ui.search

import com.example.epigram.arch.android.BasePresenter

class SearchPresenter (searchView: SearchMvp.View) : BasePresenter<SearchMvp.View>(searchView), SearchMvp.Presenter {

    override fun onCreate() {
        view?.setClickables()
    }
}