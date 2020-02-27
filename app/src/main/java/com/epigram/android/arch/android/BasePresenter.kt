package com.epigram.android.arch.android

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<T: BaseMvp.View> constructor(var view: T?) : BaseMvp.Presenter {
    protected val subscription = CompositeDisposable();

    override fun onDestroy() {
        subscription.clear()
        view = null
    }
}