package com.epigram.android.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.epigram.android.R
import com.epigram.android.data.arch.android.BaseFragment
import com.epigram.android.data.arch.utils.LoadNextPage
import com.epigram.android.data.model.Post
import com.epigram.android.ui.adapters.AdapterArticles
import com.epigram.android.ui.adapters.AdapterArticlesCorona
import com.epigram.android.ui.adapters.MyAdapterPlaceholder
import com.epigram.android.ui.article.ArticleActivity
import kotlinx.android.synthetic.main.fragment_main.*

class TabFragment : BaseFragment<TabMvp.Presenter>(), TabMvp.View, LoadNextPage {

    private var pageNum = FIRST_INDEX
    private var tabNum = 0
    private var adapter: AdapterArticles? = null
    private var adapterHome: AdapterArticlesCorona? = null

    companion object {

        const val ARG_SECTION_NUMBER = "section_number"
        const val ARG_TAB_NUM = "tab_num.int"

        const val FIRST_INDEX = 1

        var loaded = false

        fun newInstance(index: Int, position: Int) : TabFragment {
            val fragment = TabFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            bundle.putInt(ARG_TAB_NUM, position)
            fragment.arguments = bundle
            fragment.tabNum = position
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabNum = arguments!!.getInt(ARG_TAB_NUM)
        my_recycler_view.layoutManager = LinearLayoutManager(activity)
        my_recycler_view.itemAnimator = DefaultItemAnimator()
        if(my_recycler_view.adapter == null) {
            if(tabNum == 0) {
                if(adapterHome == null) {
                    my_recycler_view.adapter = MyAdapterPlaceholder()
                } else {
                    my_recycler_view.adapter = adapterHome
                }
            } else {
                if(adapter == null) {
                    my_recycler_view.adapter = MyAdapterPlaceholder()
                } else {
                    my_recycler_view.adapter = adapter
                }
            }
        }


        swipe_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorAccentHint)
        swipe_refresh.setProgressBackgroundColorSchemeResource(R.color.progress_background)
        swipe_refresh.setOnRefreshListener {
            pageNum = FIRST_INDEX
            presenter.load(pageNum, tabNum, getString(arguments!!.getInt(ARG_SECTION_NUMBER)))
        }

        presenter = TabPresenter(this)
        presenter.load(pageNum, tabNum, getString(arguments!!.getInt(ARG_SECTION_NUMBER)))
    }

    override fun onPostSuccess(posts: List<Post>) {
        pageNum++
        loaded = true
        tab_something_wrong.visibility = View.GONE
        swipe_refresh.isRefreshing = false
        if(adapter == null) {
            adapter = AdapterArticles(context!!, posts.toMutableList(), this, tabNum)
            my_recycler_view.adapter = adapter
        } else {
            if(pageNum == FIRST_INDEX + 1) (adapter as AdapterArticles).clear()
            adapter!!.addPosts(posts)
        }
    }

    override fun onPostSuccessHome(posts: List<Post>) {
        pageNum++
        loaded = true
        tab_something_wrong.visibility = View.GONE
        swipe_refresh.isRefreshing = false
        if(adapterHome == null) {
            adapterHome = AdapterArticlesCorona(context!!, emptyList<Post>().toMutableList(), posts.toMutableList(), this, tabNum)
            my_recycler_view.adapter = adapterHome
        } else {
            if(pageNum == FIRST_INDEX + 1) (adapterHome as AdapterArticlesCorona).clear()
            adapterHome!!.addPosts(posts)
        }
    }

    override fun onPostSuccessHomeMore(posts: List<Post>) {
        pageNum++
        loaded = true
        tab_something_wrong.visibility = View.GONE
        swipe_refresh.isRefreshing = false
        if(adapterHome == null) {
            adapterHome = AdapterArticlesCorona(context!!, emptyList<Post>().toMutableList(), posts.toMutableList(), this, tabNum)
            my_recycler_view.adapter = adapterHome
        } else {
            if(pageNum == FIRST_INDEX + 1) (adapterHome as AdapterArticlesCorona).clear()
            adapterHome!!.addPosts(posts)
        }
    }

    override fun onPostSuccessCorona(corona: List<Post>, posts: List<Post>) {
        pageNum++
        loaded = true
        tab_something_wrong.visibility = View.GONE
        swipe_refresh.isRefreshing = false
        if(adapterHome == null) {
            adapterHome = AdapterArticlesCorona(context!!, corona.toMutableList(), posts.toMutableList(), this, tabNum)
            my_recycler_view.adapter = adapterHome
        } else {
            if(pageNum == FIRST_INDEX + 1) (adapterHome as AdapterArticlesCorona).clear()
            adapterHome!!.addPosts(posts)
        }
    }

    override fun onPostError() {
        if(my_recycler_view.adapter is MyAdapterPlaceholder) {
            (my_recycler_view.adapter as MyAdapterPlaceholder).clear()
            tab_something_wrong.visibility = View.VISIBLE
        }
        swipe_refresh.isRefreshing = false
    }

    override fun bottomReached() {
        if(!loaded) return
        else {
            loaded = false
            presenter.load(pageNum, tabNum, getString(arguments!!.getInt(ARG_SECTION_NUMBER)))
        }
    }

    override fun onPostClicked(clicked: Post, titleImage: ImageView?) {
        if (titleImage != null) {
            ArticleActivity.start(activity!!, clicked, titleImage)
        } else {
            ArticleActivity.start(activity!!, clicked)
        }
    }

    fun reselected() {
        my_recycler_view.smoothScrollToPosition(0)
    }

    override fun finish() {

    }
}