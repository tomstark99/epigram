package com.epigram.android.data.managers

import com.epigram.android.data.model.Post
import io.reactivex.Single

interface PostManager {
    fun getPosts(page: Int, filter: String?): Single<List<Post>>
    fun getPostsBreaking(): Single<List<Post>>
    fun getPostTitles(page: Int, searchTerm: String): Single<Pair<String, List<Post>>>
    fun getSearchTotal(searchTerm: String): Single<Int>
    fun getArticle(id: String): Single<Post>
}