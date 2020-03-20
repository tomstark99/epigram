package com.epigram.android.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EpigramService {

    @GET("ghost/api/v2/content/posts")
    fun getPostsFilter(@Query("key") key: String, @Query("include") include: String, @Query("filter") fiter: String?, @Query("limit") limit: String, @Query("page") page: Int, @Query("order") order: String): Single<Wrapper<PostTemplate>>

    @GET("ghost/api/v2/content/posts")
    fun getPostsBreak(@Query("key") key: String, @Query("include") include: String, @Query("filter") fiter: String?, @Query("limit") limit: String, @Query("order") order: String): Single<Wrapper<PostTemplate>>

    @GET("ghost/api/v2/content/posts")
    fun getSearchIDs(@Query("key") key: String, @Query("include") include: String, @Query("limit") theLimit: String, @Query("fields") fields: String?, @Query("page") page: Int, @Query("order") order: String): Single<Wrapper<SearchResult>>

    @GET("ghost/api/v2/content/posts/{id}")
    fun getPostFromNotification(@Path("id") id: String, @Query("key") key: String, @Query("include") include: String): Single<Wrapper<PostTemplate>>

}
