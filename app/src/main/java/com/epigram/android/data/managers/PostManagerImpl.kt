package com.epigram.android.data.managers

import com.epigram.android.BuildConfig
import com.epigram.android.data.api.epigram.EpigramService
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
            .getPostsFilter(KEY, "tags,authors", tagFilter, "20", page, "published_at desc").map { body ->
            val posts = ArrayList<Post>()

            for (post in body.posts) {
                Post.fromTemplate(post)?.let { posts.add(it) }
            }
            posts
        }
    }


    override fun getPostsBreaking(): Single<List<Post>> {

        return service
            .getPostsBreak(KEY, "tags,authors", "tag:breaking-news", "5", "published_at desc").map { body ->
                val posts = ArrayList<Post>()

                for (post in body.posts) {
                    Post.fromTemplate(post)?.let { posts.add(it) }
                }
                posts
            }
    }

    override fun getPostsRelated(filter: String?): Single<List<Post>> {
        var tagFilter: String? = null
        if (filter != null && filter.isNotBlank()) {
            tagFilter = "tag:$filter"
        }
        return service
            .getPostsBreak(KEY, "tags,authors", tagFilter, "10", "published_at desc").map { body ->
                val posts = ArrayList<Post>()

                for (post in body.posts) {
                    Post.fromTemplate(post)?.let { posts.add(it) }
                }
                posts
            }
    }

    override fun getPostsAuthor(page: Int, author: String?): Single<List<Post>> {
        var authorFilter: String? = null
        if (author != null && author.isNotBlank()) {
            authorFilter = "author:$author"
        }
        return service
            .getPostsFilter(KEY, "tags,authors", authorFilter, "20", page, "published_at desc").map { body ->
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
                            "tags,authors",
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

    override fun getPostsById(page: Int, ids: List<String>): Single<List<Post>> {
        return service
            .getPostsFilter(KEY, "tags,authors", "id:[${ids.joinToString(",")}]", "20", page, "published_at desc").map { body ->
                val posts = ArrayList<Post>()

                for (post in body.posts) {
                    Post.fromTemplate(post)?.let { posts.add(it) }
                }
                posts
            }
    }

    override fun getPostsLiked(page: Int, tags: List<String>, authors: List<String>): Single<List<Post>> {
        return service
            .getPostsFilter(KEY, "tags,authors", "tag:[${tags.joinToString(",")}],author:[${authors.joinToString(",")}]", "20", page, "published_at desc").map { body ->
                val posts = ArrayList<Post>()

                for (post in body.posts) {
                    Post.fromTemplate(post)?.let { posts.add(it) }
                }
                posts
            }
    }

    override fun getArticle(id: String): Single<Post> {
        return service
            .getPostFromNotification(id, KEY, "tags,authors").map { it.posts.first() }.map {
                Post.fromTemplate(it)!!
            }
    }
}
