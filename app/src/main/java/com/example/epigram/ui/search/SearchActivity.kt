package com.example.epigram.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.epigram.PostAdapter
import com.example.epigram.R
import com.example.epigram.arch.android.BaseActivity
import com.example.epigram.arch.hideKeyboard
import com.example.epigram.data.models.Post
import com.example.epigram.ui.article.ArticleActivity
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit

class SearchActivity : BaseActivity<SearchPresenter>() {

    private val adapterArticles = PostAdapter(null, this::onPostClicked)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        presenter = SearchPresenter(this)

        search_recycler_view.layoutManager = LinearLayoutManager(this)
        search_recycler_view.itemAnimator = DefaultItemAnimator()
        search_recycler_view.adapter = adapterArticles
        search_recycler_view.isNestedScrollingEnabled = false

        search_back.setOnClickListener { finish() }

        val searchButtonPress = RxView.clicks(search_button_in_search_activity).doOnNext {
            if (search_recycler_view.hasFocus()) {
                search_recycler_view.smoothScrollToPosition(0)
            } else {
                hideKeyboard()
                search_recycler_view.requestFocus()
            }
        }

        val searchKeyboardPress = RxTextView.editorActions(search_query).doOnNext {
            hideKeyboard()
            search_recycler_view.requestFocus()
        }

        Observable.merge(searchButtonPress, searchKeyboardPress, RxTextView.textChanges(search_query))
            .map { search_query.text.toString() }
            .filter { charSequence ->
                if (charSequence.isEmpty()) {
                    search_no_result.visibility = View.GONE
                    search_progress.visibility = View.GONE
                    adapterArticles.clear()
                    search_placeholder.visibility = View.VISIBLE
                }
                charSequence.length > 2
            }
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { charSequence ->
                search_no_result.visibility = View.GONE
                search_placeholder.visibility = View.GONE
                if (adapterArticles.posts.isEmpty()) {
                    search_progress.visibility = View.VISIBLE
                }
                presenter.search(charSequence)
            }
            .addTo(disposable)
    }

    fun showError() {
        //TODO Improve the error message
        Snackbar.make(search_back, "Error getting results", Snackbar.LENGTH_LONG)
    }

    fun showResults(posts: List<Post>) {
        search_progress.visibility = View.GONE
        adapterArticles.totalResults = posts.size
        adapterArticles.posts = posts
    }

    private fun onPostClicked(clicked: Post, titleImage: ImageView?) {
        ArticleActivity.start(this, clicked, titleImage)
    }

    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, SearchActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
