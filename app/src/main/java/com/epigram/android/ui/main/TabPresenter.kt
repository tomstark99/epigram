package com.epigram.android.ui.main

import android.util.Log
import com.epigram.android.data.DataModule
import com.epigram.android.data.arch.android.BasePresenter
import com.epigram.android.data.managers.PostManager
import com.epigram.android.data.model.Post
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

typealias p = List<Post>

class TabPresenter (view: TabMvp.View,
                    private val postManager: PostManager = DataModule.postManager) : BasePresenter<TabMvp.View>(view), TabMvp.Presenter {

    override fun load(pageNum: Int, tabNum: Int, tab: String) {
        if(tabNum == 0) {
            if(pageNum == 1) {
                var single =
                    Single.zip(postManager.getPostsBreaking(), postManager.getPosts(pageNum, tab),
                        BiFunction<p, p, Pair<p, p>> { t1, t2 ->
                            t1 to t2
                        })
                var singleC = Single.zip(postManager.getPostsBreaking(),
                    postManager.getPostTitles(pageNum, "coronavirus"),
                    postManager.getPosts(pageNum, tab),
                    Function3<p, Pair<String, p>, p, Triple<p, p, p>> { t1, t2, t3 ->
                        Triple(t1, t2.second, t3)
                    })
                getPostsHome(single, pageNum, tab)
                //getPostsHomeC(singleC, pageNum, tab)
            }
            else{
                getMorePostsHome(pageNum, tab)
            }
        }
        else if(tabNum == 1) {
            getPostsCorona(pageNum, tab)
        }
        else {
            getPosts(pageNum, tab)
        }
    }

    fun getPostsHome(single: Single<Pair<p, p>>, pageNum: Int, tab: String) {
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
                var breaking = posts.first[0]
                var news = posts.second.toMutableList()
                news.remove(breaking)
                news.add(0, breaking)
                view?.onPostSuccessHome(news)
            }, { e ->
                view?.onPostError()
                Log.e("error", "something went wrong loading posts", e)
            }).addTo(subscription)
    }

    fun getPostsCorona(pageNum: Int, tab: String) {
        postManager.getPostTitles(pageNum, tab)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
                view?.onPostSuccessCorona(posts.second)
            }, { e ->
                view?.onPostError()
                Log.e("error", "something went wrong loading posts", e)
            }).addTo(subscription)
    }

    fun getMorePostsHome(pageNum: Int, tab: String) {
        postManager.getPosts(pageNum, tab)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
                view?.onPostSuccessHomeMore(posts)
            }, { e ->
                view?.onPostError()
                Log.e("error", "something went wrong loading posts", e)
            }).addTo(subscription)
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
}