package com.epigram.android.ui.saved

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

class SavedPresenter (view: SavedMvp.View, private val postManager: PostManager = DataModule.postManager, private val savedPosts: Preference<MutableSet<String>> = PreferenceModule.savedPosts) : BasePresenter<SavedMvp.View>(view), SavedMvp.Presenter {

    override fun load(pageNum: Int) {
        view?.setClickables()
        getPosts(pageNum, savedPosts.get().toList())
    }

    fun getPosts(pageNum: Int, ids: List<String>) {
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