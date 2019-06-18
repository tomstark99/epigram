package com.example.epigram.data

import io.reactivex.Single
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PostManager {

    fun getPosts(page: Int, filter: String?): Single<List<Post>>{
        var tagFilter: String? = null
        if(filter != null && filter.isNotBlank()){
            tagFilter = "tag:$filter"
        }
        return Single.create{
            InternetModule.getEpigramService().getPostsFilter("89c0bcf0d0e9935465c6e0f0cb", "tags", tagFilter,"20", page).enqueue(object :
                Callback<postTemplateWrapper> {
                override fun onResponse(call: Call<postTemplateWrapper>, response: Response<postTemplateWrapper>) {
                    val body = response.body()
                    val posts = ArrayList<Post>()

                    for (post in body!!.posts) {
                        Post.fromTemplate(post)?.let { posts.add(it) }
                    }

                    it.onSuccess(posts)
                }

                override fun onFailure(call: Call<postTemplateWrapper>, t: Throwable) {
                    it.onError(t)
                }
            })
        }

    }

}