package com.epigram.android.ui.article

import android.provider.ContactsContract
import android.util.Log
import com.epigram.android.data.DataModule
import com.epigram.android.data.arch.PreferenceModule
import com.epigram.android.data.arch.android.BasePresenter
import com.epigram.android.data.managers.KeywordManager
import com.epigram.android.data.managers.PostManager
import com.epigram.android.data.managers.ViewManager
import com.f2prateek.rx.preferences2.Preference
import com.google.android.gms.tasks.CancellationTokenSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class ArticlePresenter (view: ArticleMvp.View,
                        private val postManager: PostManager = DataModule.postManager,
                        private val gaManager: ViewManager = DataModule.gaManager,
                        private val keywords: Preference<MutableSet<String>> = PreferenceModule.keywords,
                        private val keywordManager: KeywordManager = DataModule.keywordManager) : BasePresenter<ArticleMvp.View>(view), ArticleMvp.Presenter {

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

    override fun updateKeywords() {
        keywordManager.generateKeywordsFromLiked()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ updatedKeywords ->
//                updatedKeywords.forEach { Timber.d("keyword %s", it) }

                val keywordSet = HashSet(keywords.get())
                keywordSet.clear()
                keywordSet.addAll(updatedKeywords)
                keywords.set(keywordSet)
            }, { e ->
//                Timber.e(e, "error generating keywords")
            }).addTo(subscription)
    }

    override fun loadKeywords(title: String) {
        getArticleKeyWords(title)
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

    fun getArticleKeyWords(title: String) {
        keywordManager.generateKeywordsFromTitle(title)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ keywords ->
                view?.onKeywordSuccess(keywords)
            }, { e ->
//                Timber.e("something went wrong generating keywords from the title", e)
            }).addTo(subscription)
    }
}