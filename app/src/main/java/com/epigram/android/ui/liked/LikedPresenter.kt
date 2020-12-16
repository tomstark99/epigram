package com.epigram.android.ui.liked

import android.util.Log
import com.epigram.android.data.DataModule
import com.epigram.android.data.arch.PreferenceModule
import com.epigram.android.data.arch.android.BasePresenter
import com.epigram.android.data.managers.PostManager
import com.epigram.android.data.model.Post
import com.f2prateek.rx.preferences2.Preference
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class LikedPresenter (view: LikedMvp.View, private val postManager: PostManager = DataModule.postManager, private val likedPosts: Preference<MutableSet<String>> = PreferenceModule.likedPosts, private val likedTags: Preference<MutableSet<String>> = PreferenceModule.likedTags, private val likedAuthors: Preference<MutableSet<String>> = PreferenceModule.likedAuthors) : BasePresenter<LikedMvp.View>(view), LikedMvp.Presenter {

    override fun load(pageNum: Int) {
        view?.setClickables()
        getPosts(pageNum, likedTags.get().toList(), likedAuthors.get().toList())
    }

    override fun loadLiked(pageNum: Int) {
        view?.setClickables()
        getPostsLiked(pageNum, likedPosts.get().toList())
    }

    fun getPosts(pageNum: Int, tags: List<String>, authors: List<String>) {
        postManager.getPostsLiked(pageNum, tags, authors)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
                view?.onPostSuccess(posts)
            }, { e ->
                view?.onPostError()
                Log.e("error", "something went wrong loading posts", e)
            }).addTo(subscription)
    }

    fun getPostsLiked(pageNum: Int, ids: List<String>) {
        postManager.getPostsById(pageNum, ids)
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