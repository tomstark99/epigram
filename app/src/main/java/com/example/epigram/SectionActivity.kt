package com.example.epigram

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.epigram.MyAdapterArticles.SEARCH_PAGE_INDEX
import com.example.epigram.data.Layout
import com.example.epigram.data.Post
import com.example.epigram.data.PostManager
import com.google.android.material.navigation.NavigationView
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

class SectionActivity : AppCompatActivity(), MyAdapterArticles.LoadNextPage, NavigationView.OnNavigationItemSelectedListener {

    private val pManager = PostManager()
    //private val adapter2 = MyAdapterArticles(ArrayList(), this, SEARCH_PAGE_INDEX)
    private var adapter2: MyAdapterArticles? = null
    private var recyclerView: RecyclerView? = null

    private val FIRST_INDEX = 1
    private var nextPage = FIRST_INDEX
    private var loaded = false

    private var retry = 0

    private var section: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        val title = findViewById<TextView>(R.id.title)
        val typeFace = Typeface.createFromAsset(assets, "fonts/lora_bold.ttf")
        title.setTypeface(typeFace)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val titleNav = headerView.findViewById<TextView>(R.id.nav_title)
        titleNav.setTypeface(typeFace)

        navView.setNavigationItemSelectedListener(this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view_section)

        val layoutManager = Layout(this)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.isNestedScrollingEnabled = false
        if (recyclerView!!.adapter == null) {
            if (adapter2 == null) {
                recyclerView!!.adapter = MyAdapterPlaceholder()
            } else {
                recyclerView!!.adapter = adapter2
            }
        }

        findViewById<View>(R.id.section_back).setOnClickListener(View.OnClickListener { finish() })

        findViewById<View>(R.id.search_button_in_section).setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        })



    }

//    fun loadPage() {
//        getView()!!.findViewById(R.id.tab_something_wrong).setVisibility(View.GONE)
//        val tag = getArguments()!!.getInt(ARG_SECTION_NUMBER)
//        var single: Single<Pair<List<Post>, List<Post>>>? = null
//        if (pageIndex == 0 && nextPage == 1) {
//            single = Single.zip(
//                pManager.getPosts(nextPage, getString(tag)),
//                pManager.getPostsBreaking(),
//                { posts, posts2 -> Pair<List<Post>, List<Post>>(posts, posts2) })
//        } else {
//            single = pManager.getPosts(nextPage, getString(tag))
//                .map { posts -> Pair<List<Post>, List<Post>>(posts, ArrayList()) }
//        }
//        single//.retry(1)
//        !!
//        .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ posts ->
//                loaded = true
//                swipeRefresh.setRefreshing(false)
//                if (!posts.second.isEmpty()) {
//                    posts.first.removeAll(posts.second)
//                    posts.first.add(0, posts.second.get(0))
//                }
//                nextPage++
//                if (adapter2 == null) {
//                    adapter2 = MyAdapterArticles(posts.first, this@PlaceholderFragment, pageIndex)
//                    recyclerView.setAdapter(adapter2)
//                } else {
//                    if (nextPage == FIRST_INDEX + 1) adapter2.clear()
//                    adapter2.addPosts(posts.first)
//                }
//            }, { e ->
//                Log.e("e", "e", e)
//                if (recyclerView.getAdapter() is MyAdapterPlaceholder) {
//                    (recyclerView.getAdapter() as MyAdapterPlaceholder).clear()
//                    getView()!!.findViewById(R.id.tab_something_wrong).setVisibility(View.VISIBLE)
//                }
//
//                swipeRefresh.setRefreshing(false)
//            })
//    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        when (menuItem.itemId) {
            R.id.nav_home -> finish()
            R.id.nav_news -> startActivity(Intent(this, SectionActivity::class.java))
            R.id.nav_features -> {
            }
            R.id.nav_comment -> {
            }
            R.id.nav_the_croft -> {
            }
            R.id.nav_opinion -> {
            }
            R.id.nav_science -> {
            }
            R.id.nav_wellbeing -> {
            }
            R.id.nav_lifestyle -> {
            }
            R.id.nav_entertainment -> {
            }
            R.id.nav_sport -> {
            }
            R.id.nav_intramural -> {
            }
            R.id.nav_puzzles -> {
            }
            R.id.nav_settings -> {
            }
            R.id.nav_about -> {
            }
            else ->

                drawerLayout.closeDrawer(GravityCompat.START)
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    fun start(context: Context) {
        val intent = Intent(context, SearchActivity::class.java)
        context.startActivity(intent)
    }

    override fun bottomReached() {
        if (!loaded) return
        loaded = false
        //recyclerView!!.post { allPostTitles(searchText!!.text.toString()) }
    }

    override fun onPostClicked(clicked: Post, titleImage: ImageView?) {
        if (titleImage != null) {
            ArticleActivity.start(this, clicked, titleImage)
        } else {
            ArticleActivity.start(this, clicked)
        }
    }
}