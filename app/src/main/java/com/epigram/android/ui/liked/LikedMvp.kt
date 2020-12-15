package com.epigram.android.ui.liked

import com.epigram.android.data.arch.android.BaseMvp
import com.epigram.android.data.model.Post

interface LikedMvp : BaseMvp {

    interface View : BaseMvp.View {
        fun onPostSuccess(posts: List<Post>)
        fun onPostError()
        fun setClickables()
    }

    interface Presenter : BaseMvp.Presenter {
        fun load(pageNum: Int)
    }
}