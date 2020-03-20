package com.epigram.android.data.arch.android

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity<T: BaseMvp.Presenter>: AppCompatActivity(), BaseMvp.View {
    lateinit var presenter: T
    protected val subscription = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
        subscription.clear()
    }
}