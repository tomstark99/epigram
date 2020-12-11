package com.epigram.android.ui.author

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.epigram.android.R
import com.epigram.android.data.arch.android.BaseActivity
import com.epigram.android.data.arch.utils.LoadNextPage
import com.epigram.android.data.model.Post
import com.epigram.android.ui.adapters.*
import com.epigram.android.ui.article.ArticleActivity
import com.epigram.android.ui.search.SearchActivity
import kotlinx.android.synthetic.main.app_bar_section.*

class AuthorActivity : BaseActivity<AuthorMvp.Presenter>(), AuthorMvp.View, LoadNextPage {

    private var pageNum = FIRST_INDEX
    private var section: String = ""
    private var tag1: String = ""
    private var adapter: AdapterArticles? = null
    private var loaded = false


    companion object {

        const val ARG_SECTION = "section.object"
        const val ARG_SECTION_TAG = "tag.object"

        const val FIRST_INDEX = 1


        fun start(context: Activity, section: String, tag: String) {
            val intent = Intent(context, AuthorActivity::class.java)
            intent.putExtra(ARG_SECTION, section)
            intent.putExtra(ARG_SECTION_TAG, tag)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_bar_section)

        section = intent.getSerializableExtra(ARG_SECTION) as String
        tag1 = intent.getSerializableExtra(ARG_SECTION_TAG) as String

        recycler_view_section.layoutManager = LinearLayoutManager(this)
        recycler_view_section.itemAnimator = DefaultItemAnimator()
        if(recycler_view_section.adapter == null) {
            if(adapter == null) {
                recycler_view_section.adapter = AdapterPlaceholder()
            } else {
                recycler_view_section.adapter = adapter
            }
        }

        swipe_refresh.setColorSchemeResources(R.color.red_to_white)
        swipe_refresh.setProgressBackgroundColorSchemeResource(R.color.progress_background)
        swipe_refresh.setOnRefreshListener {
            pageNum = FIRST_INDEX
            section_something_wrong.visibility = View.GONE
            presenter.load(pageNum, tag1)
        }

        presenter = AuthorPresenter(this)
        section_something_wrong.visibility = View.GONE
        presenter.load(pageNum, tag1)
    }

    override fun setClickables() {
        val title = findViewById<TextView>(R.id.title)
        val typeFace = Typeface.createFromAsset(assets, "fonts/lora_bold.ttf")
        title.text = getString(R.string.author, section.toLowerCase())
        title.setTypeface(typeFace)
        title.setOnClickListener { recycler_view_section.smoothScrollToPosition(0) }

        section_back.setOnClickListener{ finish() }
        search_button_in_section.setOnClickListener{
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    override fun onPostSuccess(posts: List<Post>) {
            pageNum++
            loaded = true
            swipe_refresh.isRefreshing = false
            if (adapter == null) {
                adapter = AdapterArticles(this, posts.toMutableList(), this, 0)
                recycler_view_section.adapter = adapter
            } else {
                if (pageNum == FIRST_INDEX + 1) (adapter as AdapterArticles).clear()
                adapter!!.addPosts(posts)
            }
    }

    override fun onPostError() {
        if (recycler_view_section.adapter is AdapterPlaceholder) {
            (recycler_view_section.adapter as AdapterPlaceholder).clear()
            section_something_wrong.visibility = View.VISIBLE
        }
        swipe_refresh.isRefreshing = false
    }

    override fun bottomReached() {
        if(!loaded) return
        else {
            loaded = false
            section_something_wrong.visibility = View.GONE
            presenter.load(pageNum, tag1)
        }
    }

    override fun onPostClicked(clicked: Post, titleImage: ImageView?) {
        if (titleImage != null) {
            ArticleActivity.start(this, clicked, titleImage)
        } else {
            ArticleActivity.start(this, clicked)
        }
    }

    fun reselected() {
        recycler_view_section.smoothScrollToPosition(0)
    }

}