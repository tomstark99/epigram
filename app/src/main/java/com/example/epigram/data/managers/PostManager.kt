package com.example.epigram.data.managers

import com.example.epigram.BuildConfig
import com.example.epigram.arch.InternetModule
import com.example.epigram.data.api.EpigramService
import com.example.epigram.data.models.Post
import com.example.epigram.data.templates.PostTemplate
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.*

class PostManager(private val service: EpigramService = InternetModule.epigramService) {

    private val templateToModel = { posts: List<PostTemplate> ->
        posts.mapNotNull { Post.fromTemplate(it) }
    }

    fun getNews(page: Int): Single<List<Post>> {
        return service.all(page).map(templateToModel)
    }

    fun getTaggedNews(filter: String): Single<List<Post>> {
        return service.tag(filter).map(templateToModel)
    }

    fun getBreakingNews(): Single<List<Post>> {
        return service.tag("breaking-news").map(templateToModel)
    }

    fun search(searchTerm: String): Single<Pair<String, List<Post>>> {
        return service.search(searchTerm).map(templateToModel).map { searchTerm to it }
    }

    fun getArticle(id: String): Maybe<Post> {
        return service.article(id).flatMapMaybe {
            val post = Post.fromTemplate(it)
            if (post == null) {
                Maybe.empty()
            } else {
                Maybe.just(post)
            }
        }
    }
}
