package com.epigram.android.ui.article

import android.util.Log
import com.epigram.android.data.DataModule
import com.epigram.android.data.arch.android.BasePresenter
import com.epigram.android.data.managers.PostManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class ArticlePresenter (view: ArticleMvp.View, private val postManager: PostManager = DataModule.postManager) : BasePresenter<ArticleMvp.View>(view), ArticleMvp.Presenter {

    override fun load(tag: String) {
        view?.setClickables()
        getPosts(tag)
    }

    fun getPosts(tag: String) {
        postManager.getPostsRelated(tag)
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