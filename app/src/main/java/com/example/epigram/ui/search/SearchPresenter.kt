package com.example.epigram.ui.search

import com.example.epigram.arch.DataModule
import com.example.epigram.arch.android.BasePresenter
import com.example.epigram.data.managers.PostManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SearchPresenter(
    view: SearchActivity,
    private val manager: PostManager = DataModule.postManager
) : BasePresenter<SearchActivity>(view) {

    private lateinit var latestSearch: String

    fun search(searchTerm: String) {
        latestSearch = searchTerm
        manager.search(searchTerm)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ results ->
                if (results.first == latestSearch) {
                    view?.showResults(results.second)
                }
            }, {
                Timber.e(it, "searching")
                view?.showError()
            }).addTo(compositeDisposable)
    }
}