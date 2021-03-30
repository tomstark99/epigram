package com.epigram.android.ui.article

import android.util.Log
import com.epigram.android.data.DataModule
import com.epigram.android.data.arch.android.BasePresenter
import com.epigram.android.data.managers.PostManager
import com.epigram.android.data.managers.ViewManager
import com.google.android.gms.tasks.CancellationTokenSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class ArticlePresenter (view: ArticleMvp.View, private val postManager: PostManager = DataModule.postManager, private val gaManager: ViewManager = DataModule.gaManager) : BasePresenter<ArticleMvp.View>(view), ArticleMvp.Presenter {

    private var token = ""
    private lateinit var path: String

    override fun load(tag: String) {
        view?.setClickables()
        getPosts(tag)
    }

    override fun loadViews(path: String) {
        this.path = path
        if (token.isEmpty()) {
            getAccessToken()
        }
        else getPostViews(token)
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

    fun getAccessToken() {
        gaManager.getToken()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ token ->
                Log.d("Token", "${token}")
                if (!token.isNullOrEmpty()) checkTokenValidity(token)
//                else getAccessToken()
            }, { e ->
                Log.e("token error", "something went wrong getting an access token", e)
            }).addTo(subscription)
    }

    fun checkTokenValidity(token: String) {
        gaManager.validateToken(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ valid ->
                if (valid) {
                    this.token = token
                    getPostViews(token)
                    getMostRead(token)
                    Log.d("token confirmation", "token is valid")
                } else {
                    Log.d("token confirmation", "token not valid")
                }
            }, { e ->
                Log.e("validity error", "something went wrong validating the token", e)
            }).addTo(subscription)
    }

    fun getPostViews(token: String) {
        gaManager.getViews(path, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ views ->
                view?.setViewCount(views)
            }, { e ->
                Log.e("views error", "something went wrong retrieving post views", e)
            }).addTo(subscription)
    }

    fun getMostRead(token: String) {
        gaManager.getMostRead(10,token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
                Log.d("posts", "posts")
            }, { e ->
                Log.e("most read error", "something went wrong loading most read posts", e)
            }).addTo(subscription)
    }
}