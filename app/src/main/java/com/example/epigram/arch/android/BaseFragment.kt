package com.example.epigram.arch.android

import androidx.fragment.app.Fragment

abstract class BaseFragment<T : BaseMvp.Presenter> : Fragment(), BaseMvp.View {

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    protected lateinit var presenter: T

    fun onBackPressed(): Boolean{
        return false
    }
}