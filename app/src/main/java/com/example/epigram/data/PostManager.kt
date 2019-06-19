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
                Callback<Wrapper<PostTemplate>> {
                override fun onResponse(call: Call<Wrapper<PostTemplate>>, response: Response<Wrapper<PostTemplate>>) {
                    val body = response.body()
                    val posts = ArrayList<Post>()

                    for (post in body!!.posts) {
                        Post.fromTemplate(post)?.let { posts.add(it) }
                    }

                    it.onSuccess(posts)
                }

                override fun onFailure(call: Call<Wrapper<PostTemplate>>, t: Throwable) {
                    it.onError(t)
                }
            })
        }

    }

    fun getPostTitles(searchTerm: String): Single<List<Post>>{

        return Single.create<List<String>>{
            InternetModule.getEpigramService().getSearchIDs("89c0bcf0d0e9935465c6e0f0cb","authors", "all", "title,id,primary_author").enqueue(object :
                Callback<Wrapper<SearchResult>> {
                override fun onResponse(call: Call<Wrapper<SearchResult>>, response: Response<Wrapper<SearchResult>>) {
                    val body = response.body()

                    val map = body!!.posts.filter { it.title.contains(searchTerm, true) || it.primary_author.name.contains(searchTerm, true)}.map { it.id }


                    it.onSuccess(map)
                }

                override fun onFailure(call: Call<Wrapper<SearchResult>>, t: Throwable) {
                    it.onError(t)
                }
            })
        }.flatMap { ids ->
            Single.create<List<Post>>{
                InternetModule.getEpigramService().getPostsFromSearch("89c0bcf0d0e9935465c6e0f0cb", "tags","all", "id:[${ids.joinToString(",")}]").enqueue(object :
                    Callback<Wrapper<PostTemplate>> {
                    override fun onResponse(call: Call<Wrapper<PostTemplate>>, response: Response<Wrapper<PostTemplate>>) {
                        val body = response.body()
                        val posts = ArrayList<Post>()

                        for (post in body!!.posts) {
                            Post.fromTemplate(post)?.let { posts.add(it) }
                        }

                        it.onSuccess(posts)
                    }

                    override fun onFailure(call: Call<Wrapper<PostTemplate>>, t: Throwable) {
                        it.onError(t)
                    }
                })
            }
        }

    }

}