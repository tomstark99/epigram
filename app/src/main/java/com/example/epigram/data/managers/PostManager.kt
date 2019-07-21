package com.example.epigram.data.managers

import com.example.epigram.arch.InternetModule
import com.example.epigram.data.Post
import io.reactivex.Single
import java.util.*

class PostManager {

    fun getPosts(page: Int, filter: String?): Single<List<Post>> {
        var tagFilter: String? = null
        if (filter != null && filter.isNotBlank()) {
            tagFilter = "tag:$filter"
        }
        return InternetModule.getEpigramService()
            .getPostsFilter("89c0bcf0d0e9935465c6e0f0cb", "tags", tagFilter, "20", page, "published_at desc").map { body ->
            val posts = ArrayList<Post>()

            for (post in body.posts) {
                Post.fromTemplate(post)?.let { posts.add(it) }
            }
            posts
        }
    }

    fun getPostsBreaking(): Single<List<Post>> {

        return InternetModule.getEpigramService()
            .getPostsBreak("89c0bcf0d0e9935465c6e0f0cb", "tags", "tag:breaking-news", "1", "published_at desc").map { body ->
                val posts = ArrayList<Post>()

                for (post in body.posts) {
                    Post.fromTemplate(post)?.let { posts.add(it) }
                }
                posts
            }
    }

    fun getPostTitles(page: Int, searchTerm: String): Single<Pair<String, List<Post>>> {

        return InternetModule.getEpigramService()  // if you dont get all t
            .getSearchIDs("89c0bcf0d0e9935465c6e0f0cb", "authors", "200", "title,id,primary_author",page , "published_at desc").map { body ->

                body.posts.filter {
                    it.title.contains(searchTerm, true) || it.primary_author.name.contains(
                        searchTerm,
                        true
                    )
                }.map { it.id }

            }.flatMap { ids ->
                if(ids.isEmpty()) return@flatMap Single.just(searchTerm to emptyList<Post>())
                InternetModule.getEpigramService()
                        .getPostsFilter(
                            "89c0bcf0d0e9935465c6e0f0cb",
                            "tags",
                            "id:[${ids.joinToString(",")}]",
                            "200"
                        , 0, "published_at desc")
                        .map { body ->
                            val posts = ArrayList<Post>()

                            for (post in body.posts) {
                                Post.fromTemplate(post)?.let { posts.add(it) }
                            }
                            searchTerm to posts
                        }
            }
    }

    fun getSearchTotal(searchTerm: String): Single<Int>{
        return InternetModule.getEpigramService()
            .getSearchIDs("89c0bcf0d0e9935465c6e0f0cb", "authors", "all", "title,id,primary_author",1 , "published_at desc").map { body ->
                body.posts.filter {
                    it.title.contains(searchTerm, true) || it.primary_author.name.contains(
                        searchTerm,
                        true
                    )
                }.size
            }
    }

    fun getArticle(id: String): Single<Post> {
        return InternetModule.getEpigramService()
            .getPostFromNotification(id, "89c0bcf0d0e9935465c6e0f0cb", "tags").map { it.posts.first() }.map {
                Post.fromTemplate(it)!!
            }
    }
}
