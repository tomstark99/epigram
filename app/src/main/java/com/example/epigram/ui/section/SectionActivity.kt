package com.example.epigram.ui.section

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.epigram.ui.article.ArticleActivity
import com.example.epigram.ui.adapters.MyAdapterPlaceholder
import com.example.epigram.ui.adapters.MyAdapterSection
import com.example.epigram.R
import com.example.epigram.data.Layout
import com.example.epigram.data.model.Post
import com.example.epigram.data.PostManager
import com.example.epigram.ui.search.SearchActivity
import com.google.android.material.navigation.NavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SectionActivity : AppCompatActivity(), MyAdapterSection.LoadNextPage, NavigationView.OnNavigationItemSelectedListener {

    private val pManager = PostManager()
    private var adapter2: MyAdapterSection? = null
    private var recyclerView: RecyclerView? = null

    private val FIRST_INDEX = 1
    private var nextPage = FIRST_INDEX
    private var loaded = false

    private var section: String = ""
    private var tag: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_bar_section)

        section = intent.getSerializableExtra(ARG_SECTION) as String
        tag = intent.getSerializableExtra(ARG_SECTION_TAG) as String

        val title = findViewById<TextView>(R.id.title)
        val typeFace = Typeface.createFromAsset(assets, "fonts/lora_bold.ttf")
        title.setText(section.toLowerCase())
        title.setTypeface(typeFace)

        //val navView = findViewById<NavigationView>(R.id.nav_view)
        //val headerView = navView.getHeaderView(0)
        //val titleNav = headerView.findViewById<TextView>(R.id.nav_title)
        //titleNav.setTypeface(typeFace)
        //navView.setNavigationItemSelectedListener(this)

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

        title.setOnClickListener { recyclerView!!.smoothScrollToPosition(0) }

        findViewById<View>(R.id.section_back).setOnClickListener{ finish() }
        findViewById<View>(R.id.search_button_in_section).setOnClickListener{
            startActivity(Intent(this, SearchActivity::class.java))
        }

        loadPage(tag)
    }

    fun loadPage(tag: String) {
        pManager.getPosts(nextPage, tag)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
                loaded = true
                nextPage++
                if(adapter2 == null){
                    adapter2 =
                        MyAdapterSection(this, posts, this, section)
                    recyclerView!!.adapter = adapter2
                }
                else{
                    adapter2!!.addPosts(posts)
                }
            }, { e -> Log.e("error", "soemthing went wrong loading section posts", e)})
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        when (menuItem.itemId) {
            //R.id.nav_home -> finish()
            R.id.nav_news -> startActivity(Intent(this, SectionActivity::class.java))
            R.id.nav_features -> {
            }
            R.id.nav_comment -> {
            }
            R.id.nav_the_croft -> {
            }
//            R.id.nav_opinion -> { }
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
//            R.id.nav_settings -> {
//            }
            R.id.nav_about -> {
            }
            else ->

                drawerLayout.closeDrawer(GravityCompat.START)
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun bottomReached() {
        if (!loaded) return
        loaded = false
        loadPage(tag)
    }

    override fun onPostClicked(clicked: Post, titleImage: ImageView?) {
        if (titleImage != null) {
            ArticleActivity.start(this, clicked, titleImage)
        } else {
            ArticleActivity.start(this, clicked)
        }
    }

    companion object {

        const val ARG_SECTION = "section.object"
        const val ARG_SECTION_TAG = "tag.object"

        fun start(context: Activity, section: String, tag: String) {
            val intent = Intent(context, SectionActivity::class.java)
            intent.putExtra(ARG_SECTION, section)
            intent.putExtra(ARG_SECTION_TAG, tag)
            context.startActivity(intent)

        }
    }
}