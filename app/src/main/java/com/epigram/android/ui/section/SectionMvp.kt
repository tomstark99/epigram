package com.epigram.android.ui.section

import com.epigram.android.data.arch.android.BaseMvp
import com.epigram.android.data.model.Post

interface SectionMvp : BaseMvp {

    interface View : BaseMvp.View {
        fun onPostSuccess(posts: List<Post>)
        fun onPostSuccessCorona(corona: List<Post>)
        fun onPostError()
        fun setClickables()
    }

    interface Presenter : BaseMvp.Presenter {
        fun load(pageNum: Int, section: String)
        fun loadC(pageNum: Int, section: String)
    }
}