package com.epigram.android.data.api

import com.epigram.android.BuildConfig
import com.epigram.android.data.api.EpigramService
import com.epigram.android.data.arch.AppModule
import com.google.gson.Gson

import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

import java.util.concurrent.TimeUnit

object ApiModule {

    private const val URL = BuildConfig.URL
    private const val CACHE_SIZE_BYTES = 1024 * 1024 * 2L

    val apiService: EpigramService by lazy {
        retrofit.create(EpigramService::class.java)
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private val okhttp by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor()
            .setLevel(Level.BODY))
            .cache(Cache(AppModule.application.cacheDir, CACHE_SIZE_BYTES))
            .build()
    }

    val gson by lazy {
        Gson()
    }

}
