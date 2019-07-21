package com.example.epigram.ui.main

import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.epigram.ui.article.ArticleActivity
import com.example.epigram.MyAdapterArticles
import com.example.epigram.R
import com.example.epigram.arch.android.Layout
import com.example.epigram.data.Post
import com.example.epigram.data.managers.PostManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import java.util.ArrayList

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment(), MyAdapterArticles.LoadNextPage {

    private val FIRST_INDEX = 1

    private val pManager = PostManager()

    private var adapter2: MyAdapterArticles? = null
    private var nextPage = FIRST_INDEX
    private var loaded = false
    private var recyclerView: RecyclerView? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private val breaking = ArrayList<Post>()

    private var pageIndex: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.my_recycler_view)

        val layoutManager = Layout(context)
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
        loadPage()

        swipeRefresh = view.findViewById<View>(R.id.swipe_refresh) as SwipeRefreshLayout
        swipeRefresh!!.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        swipeRefresh!!.setOnRefreshListener {
            if (adapter2 == null && recyclerView!!.adapter !is MyAdapterPlaceholder) {
                // app crashes if there is no adapter e.g. if there is no internet connection
            } else {
                nextPage = FIRST_INDEX
                //if (adapter2 != null) adapter2.clear();
                loadPage()
            }
        }


    }

    fun loadPage() {
        view!!.findViewById<View>(R.id.tab_something_wrong).visibility = View.GONE
        val tag = arguments!!.getInt(ARG_SECTION_NUMBER)
        var single: Single<Pair<List<Post>, List<Post>>>? = null
        if (pageIndex == 0 && nextPage == 1) {
            single = Single.zip<List<NonExistentClass>, List<NonExistentClass>, R>(
                pManager.getPosts(
                    nextPage,
                    getString(tag)
                ),
                pManager.getPostsBreaking(),
                { posts, posts2 -> Pair<List<NonExistentClass>, List<NonExistentClass>>(posts, posts2) })
        } else {
            single = pManager.getPosts(nextPage, getString(tag))
                .map<R> { posts -> Pair<List<NonExistentClass>, ArrayList<Any>>(posts, ArrayList()) }
        }
        single//.retry(1)
        !!
        .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
                loaded = true
                swipeRefresh!!.isRefreshing = false
                if (!posts.second.isEmpty()) {
                    posts.first.add(0, posts.second.get(0))
                }
                nextPage++
                if (adapter2 == null) {
                    adapter2 = MyAdapterArticles(posts.first, this@PlaceholderFragment, pageIndex)
                    recyclerView!!.adapter = adapter2
                } else {
                    if (nextPage == FIRST_INDEX + 1) adapter2!!.clear()
                    adapter2!!.addPosts(posts.first)
                }
            }, { e ->
                Log.e("e", "e", e)
                if (recyclerView!!.adapter is MyAdapterPlaceholder) {
                    (recyclerView!!.adapter as MyAdapterPlaceholder).clear()
                    view!!.findViewById<View>(R.id.tab_something_wrong).visibility = View.VISIBLE
                }

                swipeRefresh!!.isRefreshing = false
            })
    }

    override fun bottomReached() {
        if (!loaded) return
        loaded = false
        loadPage()

    }

    override fun onPostClicked(clicked: Post, imageView: ImageView?) {
        if (imageView != null) {
            ArticleActivity.start(activity!!, clicked, imageView)
        } else {
            ArticleActivity.start(activity!!, clicked)
        }
    }

    fun reselected() {
        recyclerView!!.smoothScrollToPosition(0)
    }

    companion object {

        private val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(index: Int, position: Int): PlaceholderFragment {
            val fragment = PlaceholderFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle
            fragment.pageIndex = position
            return fragment
        }
    }
}