package com.example.epigram.arch.android

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity<T: BaseMvp.Presenter>: AppCompatActivity() {
    lateinit var presenter: T
    protected val disposable = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
        disposable.clear()
    }
}