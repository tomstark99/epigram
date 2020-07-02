package com.epigram.android.data.managers

import com.epigram.android.BuildConfig
import com.epigram.android.data.api.EpigramService
import com.epigram.android.data.model.Post
import io.reactivex.Single
import java.util.*

class PostManagerImpl (val service: EpigramService) : PostManager{

    val KEY = BuildConfig.API_KEY

    override fun getPosts(page: Int, filter: String?): Single<List<Post>> {
        var tagFilter: String? = null
        if (filter != null && filter.isNotBlank()) {
            tagFilter = "tag:$filter"
        }
        return service
            .getPostsFilter(KEY, "tags", tagFilter, "20", page, "published_at desc").map { body ->
            val posts = ArrayList<Post>()

            for (post in body.posts) {
                Post.fromTemplate(post)?.let { posts.add(it) }
            }
            posts
        }
    }

    override fun getPostsBreaking(): Single<List<Post>> {

        return service
            .getPostsBreak(KEY, "tags", "tag:breaking-news", "5", "published_at desc").map { body ->
                val posts = ArrayList<Post>()

                for (post in body.posts) {
                    Post.fromTemplate(post)?.let { posts.add(it) }
                }
                posts
            }
    }

    override fun getPostTitles(page: Int, searchTerm: String): Single<Pair<String, List<Post>>> {

        return service  // if you dont get all t
            .getSearchIDs(KEY, "authors", "200", "title,id,primary_author",page , "published_at desc").map { body ->

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
                            KEY,
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

    override fun getSearchTotal(searchTerm: String): Single<Int>{
        return service
            .getSearchIDs(KEY, "authors", "all", "title,id,primary_author",1 , "published_at desc").map { body ->
                body.posts.filter {
                    it.title.contains(searchTerm, true) || it.primary_author.name.contains(
                        searchTerm,
                        true
                    )
                }.size
            }
    }

    override fun getArticle(id: String): Single<Post> {
        return service
            .getPostFromNotification(id, KEY, "tags").map { it.posts.first() }.map {
                Post.fromTemplate(it)!!
            }
    }
}
