package com.example.epigram.ui.search

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.epigram.R

import com.example.epigram.arch.android.BaseActivity
import com.example.epigram.arch.utils.Utils
import com.example.epigram.data.Post
import com.example.epigram.data.PostManager
import com.example.epigram.ui.about.AboutActivity
import com.example.epigram.ui.adapters.AdapterSearch
import com.example.epigram.ui.article.ArticleActivity
import com.example.epigram.ui.main.SectionsPagerAdapter
import com.example.epigram.ui.search.SearchActivity
import com.example.epigram.ui.section.SectionActivity
import com.example.epigram.ui.settings.SettingsActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.functions.Function3
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*
import java.util.concurrent.TimeUnit

class SearchActivity1 : BaseActivity<SearchMvp.Presenter>(), SearchMvp.View, AdapterSearch.LoadNextPage {

    private lateinit var adapter: AdapterSearch
    var retry = 0
    var nextPage = 1
    var lastSearch: String? = null
    var pManager = PostManager()
    var loaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        recycler_view_search.layoutManager = LinearLayoutManager(this)
        recycler_view_search.itemAnimator = DefaultItemAnimator()
        adapter = AdapterSearch(this, emptyList(), this)
        recycler_view_search.adapter = adapter
        presenter = SearchPresenter(this)
        presenter.onCreate()
    }

    override fun setClickables() {
        search_back.setOnClickListener { finish() }
        search_button_in_search_activity.setOnClickListener {
            if(search_query.text.toString().length > 2) {
                load(search_query.text.toString())
            }
        }
        val searchPress = RxView.clicks(search_button_in_search_activity)
            .doOnNext {
                if(recycler_view_search.hasFocus()) {
                    recycler_view_search.smoothScrollToPosition(0)
                }
                else {
                    Utils.hideKeyboard(this)
                    recycler_view_search.requestFocus()
                }
            }
        val searchKeyPress = RxTextView.editorActions(search_query)
            .doOnNext {
                Utils.hideKeyboard(this)
                recycler_view_search.requestFocus()
            }
        Observable.merge(searchPress, searchKeyPress, RxTextView.textChanges(search_query))
            .map(Function<Any, String>{ search_query.text.toString()})
            .filter { char ->
                if(char.length == 0) {
                    search_no_result.visibility = View.GONE
                    search_progress.visibility = View.GONE
                    adapter.clear()
                    search_placeholder.visibility = View.VISIBLE
                }
                char.length > 2
            }.throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { c ->
                search_no_result.visibility = View.GONE
                search_placeholder.visibility = View.GONE
                if(adapter.posts.isEmpty()) search_progress.visibility = View.GONE
                load(c)
            }
    }

    fun load(search: String) {
        retry++
        if (!search.equals(lastSearch)) {
            nextPage = 1
            lastSearch = search
            retry = 0
        }
        Single.zip(pManager.getPostTitles(nextPage,lastSearch!!),
                    pManager.getSearchTotal(lastSearch!!),
                BiFunction<Pair<String, List<Post>>, Int, Triple<String, List<Post>, Int>>{
                    t1, t2 -> Triple(t1.first, t1.second, t2)
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ triple ->
                    if(triple.first.equals(lastSearch)) return@subscribe
                    loaded = true
                    if(triple.second.size == 0){
                        if(retry <= 10) load(search)
                        if(retry == 11 && adapter.posts.isEmpty()){
                            if(!(search_placeholder.visibility == View.VISIBLE)){
                                search_progress.visibility = View.GONE
                                search_no_result.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        retry = 0
                        adapter.updateResults(triple.third)
                        search_progress.visibility = View.GONE
                        if(nextPage == 1){
                            adapter.initPosts(triple.second)
                        } else {
                            adapter.addPosts(triple.second)
                        }
                    }
                    nextPage++
        },{ e -> Log.e("error", "error on loading posts", e)})
    }

    override fun bottomReached() {
        if(!loaded) return
        loaded = false
        recycler_view_search.post { load(search_query.text.toString()) }
    }

    override fun onPostClicked(clicked: Post, titleImage: ImageView?) {
        if(titleImage != null) {
            ArticleActivity.Companion.start(this, clicked, titleImage)
        } else {
            ArticleActivity.Companion.start(this, clicked)
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SearchActivity1::class.java))
        }
    }

}