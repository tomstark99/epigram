package com.example.epigram.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.epigram.PostAdapter
import com.example.epigram.R
import com.example.epigram.arch.android.Layout
import com.example.epigram.data.managers.PostManager
import com.example.epigram.data.models.Post
import com.example.epigram.ui.article.ArticleActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber

//TODO This fragment should be using a ViewModel then it can persist it's posts and state when it's off screen
class PlaceholderFragment : Fragment() {

    private var disposable: CompositeDisposable = CompositeDisposable()
    private val postManager = PostManager()
    private val postAdapter = PostAdapter(this::bottomReached, this::onPostClicked)
    private var nextPage = FIRST_INDEX
    private var loaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disposable.dispose()
        disposable = CompositeDisposable()

        val layoutManager = Layout(requireContext())
        tab_recycler_view.layoutManager = layoutManager
        tab_recycler_view.itemAnimator = DefaultItemAnimator()

        tab_recycler_view.isNestedScrollingEnabled = false

        if (tab_recycler_view.adapter == null) {
            if (postAdapter.posts.isEmpty()) {
                tab_recycler_view.adapter = PlaceholderAdapter()
            } else {
                tab_recycler_view.adapter = postAdapter
            }
        }
        loadPage()

        if (arguments!!.getBoolean(ARG_HAS_BREAKING)) {
            postManager.getBreakingNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    postAdapter.breaking = it.firstOrNull()
                }, {
                    Timber.e(it, "loading breaking")
                    //Failed to load breaking, leave showing errors to loadPage though
                }).addTo(disposable)
        }

        tab_swipe_refresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        tab_swipe_refresh.setOnRefreshListener {
            if (postAdapter.posts.isEmpty() && tab_recycler_view.adapter !is PlaceholderAdapter) {
                // app crashes if there is no adapter e.g. if there is no internet connection
            } else {
                nextPage = FIRST_INDEX
                loadPage()
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        disposable.dispose()
        if (isVisibleToUser) {
            disposable = CompositeDisposable()
            if (tab_recycler_view != null) {
                loadPage()
            }
        }
    }

    private fun loadPage() {
        tab_something_wrong.visibility = View.GONE
        val tag = getString(arguments!!.getInt(ARG_SECTION_NUMBER))
        if (tag.isEmpty()) {
            postManager.getNews(nextPage)
        } else {
            postManager.getTaggedNews(tag)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ posts ->
                if (!disposable.isDisposed && tab_swipe_refresh != null) {
                    loaded = true
                    tab_swipe_refresh.isRefreshing = false
                    nextPage++
                    if (postAdapter.posts.isEmpty()) {
                        tab_recycler_view.adapter = postAdapter
                        postAdapter.posts = posts
                    } else {
                        if (nextPage == FIRST_INDEX + 1) postAdapter.posts = emptyList()
                        postAdapter.posts += posts
                    }
                }
            }, { e ->
                Timber.e(e, "loading posts (page: $nextPage)")
                if (tab_recycler_view.adapter is PlaceholderAdapter) {
                    (tab_recycler_view.adapter as PlaceholderAdapter).clear()
                    tab_something_wrong.visibility = View.VISIBLE
                }

                tab_swipe_refresh.isRefreshing = false
            })
            .addTo(disposable)
    }

    private fun bottomReached() {
        if (!loaded) return
        loaded = false
        loadPage()
    }

    private fun onPostClicked(clicked: Post, imageView: ImageView?) {
        ArticleActivity.start(requireActivity(), clicked, imageView)
    }

    fun reselected() {
        tab_recycler_view.smoothScrollToPosition(0)
    }

    companion object {
        private const val FIRST_INDEX = 1
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val ARG_HAS_BREAKING = "has_breaking.bool"

        fun newInstance(index: Int, position: Int): PlaceholderFragment {
            val fragment = PlaceholderFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            bundle.putBoolean(ARG_HAS_BREAKING, position == 0)
            fragment.arguments = bundle
            return fragment
        }
    }
}