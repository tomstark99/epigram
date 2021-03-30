package com.epigram.android.data.managers

import com.epigram.android.BuildConfig
import com.epigram.android.data.api.epigram.EpigramService
import com.epigram.android.data.api.ga.GaService
import com.epigram.android.data.model.Post
import com.epigram.android.data.model.TokenAuthenticator
import com.epigram.android.data.model.Views
import io.reactivex.Single
import java.util.ArrayList

class ViewManagerImpl (val service: GaService, val epiService: EpigramService) : ViewManager {

    private var token = String

    override fun getToken(): Single<String> {
        return service.getAccessToken(BuildConfig.GA_CLIENT_ID, BuildConfig.GA_CLIENT_SECRET, "refresh_token", BuildConfig.GA_REFRESH_TOKEN).map { body ->
            TokenAuthenticator.fromTemplate(body)?.token
        }
    }

    override fun validateToken(token: String): Single<Boolean> {
        return service.getTokenValidity(token).map { body ->
            TokenAuthenticator.fromTemplate(body)?.expiry!!.isNotEmpty()
        }
    }

    override fun getViews(path: String, token: String): Single<String> {
        return service.getPostViews("ga:176589224",
            "2015-01-01",
            "today",
            "ga:pageviews",
            "ga:pagePath=@${path}",
            token).map { body ->
            val views = Views.fromTemplate(body)
            views!!.views.first()
        }
    }

    override fun getMostRead(count: Int, token: String): Single<List<Post>> {
        return service.getMostRead("ga:176589224",
            "21daysAgo",
            "today",
            "ga:pageviews",
            "ga:pagePath",
            "-ga:pageviews",
            "ga:pagePath!=/;ga:pagePath!~^\\/tag\\/*;ga:pagePath!~^\\/page\\/*;ga:pagePath!@amp",
            "$count",
            token).map { body ->
            Views.fromTemplate(body)?.slugs
        }.flatMap { slugs ->
            if(slugs.isEmpty()) return@flatMap Single.just((emptyList<Post>()))
            epiService.getPostsFilter(BuildConfig.API_KEY, "tags,authors", "slug:[${slugs.joinToString(",")}]", "20", 0, "").map { body ->
                    val posts = ArrayList<Post>()

                    for (post in body.posts) {
                        Post.fromTemplate(post)?.let { posts.add(it) }
                    }
                    posts
                }
        }
    }

}