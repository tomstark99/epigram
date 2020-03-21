package com.epigram.android.ui.main

import com.epigram.android.data.arch.android.BaseMvp
import com.epigram.android.data.model.Post

interface TabMvp : BaseMvp {

    interface View : BaseMvp.View {
        fun onPostSuccessHome(posts: List<Post>)
        fun onPostSuccessHomeMore(posts: List<Post>)
        fun onPostSuccessCorona(corona: List<Post>)
        fun onPostSuccess(posts: List<Post>)
        fun onPostError()
    }

    interface Presenter : BaseMvp.Presenter {
        fun load(pageNum: Int, tabNum: Int, tab: String)
    }
}