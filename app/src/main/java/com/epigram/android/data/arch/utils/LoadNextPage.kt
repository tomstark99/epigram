package com.epigram.android.data.arch.utils

import android.widget.ImageView
import com.epigram.android.data.model.Post

interface LoadNextPage {
    fun bottomReached()
    fun onPostClicked(clicked: Post, titleImage: ImageView?)
}