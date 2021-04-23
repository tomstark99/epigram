package com.epigram.android.ui.section

import android.util.Log
import com.epigram.android.data.DataModule
import com.epigram.android.data.arch.android.BasePresenter
import com.epigram.android.data.managers.PostManager
import com.epigram.android.data.managers.ViewManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class SectionPresenter (view: SectionMvp.View, private val postManager: PostManager = DataModule.postManager,
                        private val gaManager: ViewManager = DataModule.gaManager) : BasePresenter<SectionMvp.View>(view), SectionMvp.Presenter {

    private var token = ""

    override fun load(pageNum: Int, section: String) {
        view?.setClickables()
        getPosts(pageNum, section)
    }

    override fun loadMR(count: Int) {
        view?.setClickables()
        if (token.isEmpty()) {
            getAccessToken(count)
        }
        else getPostsMostRead(count, token)
    }

    override fun loadC(pageNum: Int, section: String) {
        view?.setClickables()
        getPostsCorona(pageNum, section)
    }

    fun getPosts(pageNum: Int, tab: String) {
        postManager.getPosts(pageNum, tab)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
                view?.onPostSuccess(posts)
            }, { e ->
                view?.onPostError()
                Log.e("error", "something went wrong loading posts", e)
            }).addTo(subscription)
    }

    fun getAccessToken(count: Int) {
        gaManager.getToken()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ token ->
                Log.d("Token", "${token}")
                if (!token.isNullOrEmpty()) checkTokenValidity(count, token)
//                else getAccessToken()
            }, { e ->
                Log.e("token error", "something went wrong getting an access token", e)
            }).addTo(subscription)
    }

    fun checkTokenValidity(count: Int, token: String) {
        gaManager.validateToken(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ valid ->
                if (valid) {
                    this.token = token
                    getPostsMostRead(count, token)
                    Log.d("token confirmation", "token is valid")
                } else {
                    Log.d("token confirmation", "token not valid")
                }
            }, { e ->
                Log.e("validity error", "something went wrong validating the token", e)
            }).addTo(subscription)
    }

    fun getPostsMostRead(count: Int, token: String){
        gaManager.getMostRead(count, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts -> 
                view?.onPostSuccessMostRead(posts)
            }, { e ->
                view?.onPostError()
                Log.e("error", "something went wrong loading most read posts", e)
            }).addTo(subscription)
    }

    fun getPostsCorona(pageNum: Int, tab: String) {
        postManager.getPosts(pageNum, tab)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
                view?.onPostSuccessCorona(posts)
            }, { e ->
                view?.onPostError()
                Log.e("error", "something went wrong loading posts", e)
            }).addTo(subscription)
    }
}