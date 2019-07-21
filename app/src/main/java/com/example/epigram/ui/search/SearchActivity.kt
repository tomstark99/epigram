package com.example.epigram.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epigram.MyAdapterArticles
import com.example.epigram.R
import com.example.epigram.arch.android.Layout
import com.example.epigram.data.Post
import com.example.epigram.data.managers.PostManager
import com.example.epigram.ui.article.ArticleActivity
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers

import java.util.ArrayList
import java.util.concurrent.TimeUnit

import com.example.epigram.MyAdapterArticles.SEARCH_PAGE_INDEX

class SearchActivity : AppCompatActivity(), MyAdapterArticles.LoadNextPage {

    private val pManager = PostManager()
    private val adapterArticles = MyAdapterArticles(ArrayList(), this, SEARCH_PAGE_INDEX)
    private var recyclerView: RecyclerView? = null

    private var searchText: EditText? = null

    private val FIRST_INDEX = 1
    private var nextPage = FIRST_INDEX
    private var loaded = false

    private var retry = 0

    private var latestSearch: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchText = findViewById(R.id.search_query)

        recyclerView = findViewById(R.id.recycler_view_search)

        val layoutManager = Layout(this)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = adapterArticles
        recyclerView!!.isNestedScrollingEnabled = false

        findViewById<View>(R.id.search_back).setOnClickListener { finish() }

        findViewById<View>(R.id.search_button_in_search_activity).setOnClickListener {
            if (searchText!!.text.length > 2) {
                allPostTitles(searchText!!.text.toString())
            }
        }

        val searchButtonPress = RxView.clicks(findViewById(R.id.search_button_in_search_activity)).doOnNext {
            if (recyclerView!!.hasFocus()) {
                //findViewById(R.id.search_query).requestFocus();
                recyclerView!!.smoothScrollToPosition(0)
                //Utils.showKeyboard(findViewById(R.id.search_query), SearchActivity.this);
            } else {
                Utils.hideKeyboard(this@SearchActivity)
                recyclerView!!.requestFocus()
            }
        }

        val searchKeyboardPress = RxTextView.editorActions(searchText!!).doOnNext {
            Utils.hideKeyboard(this@SearchActivity)
            recyclerView!!.requestFocus()
        }

        Observable.merge(searchButtonPress, searchKeyboardPress, RxTextView.textChanges(searchText!!))
            .map { searchText!!.text.toString() }.filter { charSequence ->
            if (charSequence.length == 0) {
                findViewById<View>(R.id.search_no_result).visibility = View.GONE
                findViewById<View>(R.id.search_progress).visibility = View.GONE
                adapterArticles.clear()
                findViewById<View>(R.id.search_placeholder).visibility = View.VISIBLE
            }
            charSequence.length > 2
        }.throttleFirst(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
            .subscribe { charSequence ->
                findViewById<View>(R.id.search_no_result).visibility = View.GONE
                findViewById<View>(R.id.search_placeholder).visibility = View.GONE
                if (adapterArticles.posts.isEmpty()) {
                    findViewById<View>(R.id.search_progress).visibility = View.VISIBLE
                }
                allPostTitles(charSequence)
            }

    }


    fun allPostTitles(searchQuery: String) {
        retry++
        if (searchQuery != latestSearch) {

            nextPage = 1
            latestSearch = searchQuery
            retry = 0
        }
        Single.zip<Pair<String, List<NonExistentClass>>, Int, Triple<Int, String, List<NonExistentClass>>>(
            pManager.getPostTitles(
                nextPage,
                latestSearch!!
            ),
            pManager.getSearchTotal(latestSearch!!),
            { stringListPair, integer ->
                Triple<Int, String, List<NonExistentClass>>(
                    integer,
                    stringListPair.first,
                    stringListPair.second
                )
            }).retry(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (first, second, third) ->
                if (second != latestSearch) return@Single.zip(
                    pManager.getPostTitles(nextPage, latestSearch),
                    pManager.getSearchTotal(latestSearch),
                    stringListPair,
                    integer
                ) -> new Triple<>(integer, stringListPair.getFirst(), stringListPair.getSecond())).retry(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe
                loaded = true
                if (third.size == 0) {
                    if (retry <= 10) {
                        allPostTitles(searchQuery)
                    }
                    if (retry == 11 && adapterArticles.posts.isEmpty()) {
                        if (findViewById<View>(R.id.search_placeholder).visibility != View.VISIBLE) {
                            findViewById<View>(R.id.search_progress).visibility = View.GONE
                            findViewById<View>(R.id.search_no_result).visibility = View.VISIBLE
                        }
                    }
                } else {
                    retry = 0
                    adapterArticles.setResultTotal(first)
                    findViewById<View>(R.id.search_progress).visibility = View.GONE
                    if (nextPage == 1) {
                        adapterArticles.setPostList(third)
                    } else {
                        adapterArticles.addPosts(third)
                    }
                }
                nextPage++
            }, { e -> Log.e("e", "e", e) })
    }

    override fun bottomReached() {
        if (!loaded) return
        loaded = false
        recyclerView!!.post { allPostTitles(searchText!!.text.toString()) }
    }

    override fun onPostClicked(clicked: Post, titleImage: ImageView?) {
        if (titleImage != null) {
            ArticleActivity.start(this, clicked, titleImage)
        } else {
            ArticleActivity.start(this, clicked)
        }
    }

    companion object {


        fun start(context: Context) {
            val intent = Intent(context, SearchActivity::class.java)
            context.startActivity(intent)
        }
    }
}
