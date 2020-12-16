package com.epigram.android.ui.author

import android.util.Log
import com.epigram.android.data.DataModule
import com.epigram.android.data.arch.android.BasePresenter
import com.epigram.android.data.managers.PostManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class AuthorPresenter (view: AuthorMvp.View, private val postManager: PostManager = DataModule.postManager) : BasePresenter<AuthorMvp.View>(view), AuthorMvp.Presenter {

    override fun load(pageNum: Int, section: String) {
        view?.setClickables()
        getPosts(pageNum, section)
    }

    fun getPosts(pageNum: Int, tab: String) {
        postManager.getPostsAuthor(pageNum, tab)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
                view?.onPostSuccess(posts)
            }, { e ->
                view?.onPostError()
                Log.e("error", "something went wrong loading posts", e)
            }).addTo(subscription)
    }
}