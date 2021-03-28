package com.epigram.android.data.managers

import com.epigram.android.BuildConfig
import com.epigram.android.data.api.ga.GaService
import com.epigram.android.data.model.Post
import com.epigram.android.data.model.Views
import io.reactivex.Single

class ViewManagerImpl (val service: GaService) : ViewManager {

    private var token = String

    override fun getToken(): Single<String> {
        return service.getAccessToken(BuildConfig.GA_CLIENT_ID, BuildConfig.GA_CLIENT_SECRET, "refresh_token", BuildConfig.GA_REFRESH_TOKEN).map { body ->
            body.content.access_token
        }
    }

    override fun validateToken(token: String): Single<Boolean> {
        return service.getTokenValidity(token).map { body ->
            !body.content.expires_in.isNullOrEmpty()
        }
    }

    override fun getViews(path: String, token: String): Single<String> {
        return service.getPostViews(BuildConfig.GA_CLIENT_ID,
            "2015-01-01",
            "today",
            "ga%3Apageviews",
            "ga%3ApagePath%3D%40${path}",
            token).map { body ->
            val views = Views.fromTemplate(body.content)
            views!!.views.first()
        }
    }

    override fun getMostRead(count: Int, token: String): Single<List<Post>> {
        //
    }

}