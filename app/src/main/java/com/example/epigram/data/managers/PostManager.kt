package com.example.epigram.data.managers

import com.example.epigram.BuildConfig
import com.example.epigram.arch.InternetModule
import com.example.epigram.data.api.EpigramService
import com.example.epigram.data.models.Post
import io.reactivex.Single
import java.util.*

class PostManager(private val service: EpigramService = InternetModule.epigramService) {

    fun getPosts(page: Int, filter: String?): Single<List<Post>> {
        var tagFilter: String? = null
        if (filter != null && filter.isNotBlank()) {
            tagFilter = "tag:$filter"
        }
        return service
            .getPostsFilter(BuildConfig.API_KEY, "tags", tagFilter, "20", page, "published_at desc").map { body ->
            val posts = ArrayList<Post>()

            for (post in body.posts) {
                Post.fromTemplate(post)?.let { posts.add(it) }
            }
            posts
        }
    }

    fun getPostsBreaking(): Single<List<Post>> {

        return service
            .getPostsBreak(BuildConfig.API_KEY, "tags", "tag:breaking-news", "1", "published_at desc").map { body ->
                val posts = ArrayList<Post>()

                for (post in body.posts) {
                    Post.fromTemplate(post)?.let { posts.add(it) }
                }
                posts
            }
    }

    fun getPostTitles(page: Int, searchTerm: String): Single<Pair<String, List<Post>>> {

        return service  // if you dont get all t
            .getSearchIDs(BuildConfig.API_KEY, "authors", "200", "title,id,primary_author",page , "published_at desc").map { body ->

                body.posts.filter {
                    it.title.contains(searchTerm, true) || it.primary_author.name.contains(
                        searchTerm,
                        true
                    )
                }.map { it.id }

            }.flatMap { ids ->
                if(ids.isEmpty()) return@flatMap Single.just(searchTerm to emptyList<Post>())
                service
                        .getPostsFilter(
                            BuildConfig.API_KEY,
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
        return service
            .getSearchIDs(BuildConfig.API_KEY, "authors", "all", "title,id,primary_author",1 , "published_at desc").map { body ->
                body.posts.filter {
                    it.title.contains(searchTerm, true) || it.primary_author.name.contains(
                        searchTerm,
                        true
                    )
                }.size
            }
    }

    fun getArticle(id: String): Single<Post> {
        return service
            .getPostFromNotification(id, BuildConfig.API_KEY, "tags").map { it.posts.first() }.map {
                Post.fromTemplate(it)!!
            }
    }
}
