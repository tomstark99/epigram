package com.epigram.android.data.api.ga

import com.epigram.android.data.model.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GaService {

    @POST("oauth2/v3/token")
    fun getAccessToken(@Query("client_id") id: String,
                       @Query("client_secret") secret: String,
                       @Query("grant_type") type: String,
                       @Query("refresh_token") token: String): Single<GaWrapper<AuthenticatorTemplate>>

    @POST("oauth2/v3/tokeninfo")
    fun getTokenValidity(@Query("access_token") token: String): Single<GaWrapper<AuthenticatorTemplate>>

    @GET("analytics/v3/data/ga")
    fun getPostViews(@Query("ids") id: String,
                     @Query("start-date") startDate: String,
                     @Query("end-date") endDate: String,
                     @Query("metrics") metrics: String,
                     @Query("filters") filter: String,
                     @Query("access_token") token: String): Single<GaWrapper<ViewTemplate>>

    @GET("analytics/v3/data/ga")
    fun getMostRead(@Query("ids") id: String,
                    @Query("start-date") startDate: String,
                    @Query("end-date") endDate: String,
                    @Query("metrics") metrics: String,
                    @Query("dimensions") dimension: String,
                    @Query("sort") sort: String,
                    @Query("filters") filter: String,
                    @Query("max-results") limit: String,
                    @Query("access_token") token: String): Single<GaWrapper<ViewTemplate>>

}