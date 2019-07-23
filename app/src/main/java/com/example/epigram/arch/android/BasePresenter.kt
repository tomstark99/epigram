package com.example.epigram.arch.android

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<T: BaseActivity<*>>(protected var view: T?) : BaseMvp.Presenter {
    protected val compositeDisposable = CompositeDisposable();

    override fun onDestroy() {
        compositeDisposable.clear()
        view = null
    }
}