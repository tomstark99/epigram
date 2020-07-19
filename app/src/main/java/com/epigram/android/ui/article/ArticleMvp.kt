package com.epigram.android.ui.article

import com.epigram.android.data.arch.android.BaseMvp
import com.epigram.android.data.model.Post

interface ArticleMvp : BaseMvp {

    interface View : BaseMvp.View {
        fun onPostSuccess(posts: List<Post>)
        fun onPostError()
        fun setClickables()
    }

    interface Presenter : BaseMvp.Presenter {
        fun load(tag: String)
    }
}