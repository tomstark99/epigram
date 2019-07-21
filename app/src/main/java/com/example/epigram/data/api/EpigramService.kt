package com.example.epigram.data.api

import com.example.epigram.data.templates.PostTemplate
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface EpigramService {

    @GET("/all/page/{page}")
    fun all(@Path("page") page: Int): Single<List<PostTemplate>>

    @GET("/search/{searchTerm}")
    fun search(@Path("searchTerm") searchTerm: String): Single<List<PostTemplate>>

    @GET("/tag/{tag}")
    fun tag(@Path("tag") tag: String): Single<List<PostTemplate>>

    @GET("/article/{id}")
    fun article(@Path("id") id: String): Single<PostTemplate>
}
