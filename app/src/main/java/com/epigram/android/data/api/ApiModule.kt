package com.epigram.android.data.api

import com.epigram.android.BuildConfig
import com.epigram.android.data.api.epigram.EpigramService
import com.epigram.android.data.api.ga.GaService
import com.epigram.android.data.arch.AppModule
import com.google.gson.Gson

import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit

object ApiModule {

    private const val URL_EPI = BuildConfig.URL
    private const val URL_GA = BuildConfig.GA_URL

    private const val CACHE_SIZE_BYTES = 1024 * 1024 * 2L

    val apiServiceEpigram: EpigramService by lazy {
        retrofitEpi.create(EpigramService::class.java)
    }
    val apiServiceGa: GaService by lazy {
        retrofitGa.create(GaService::class.java)
    }

    private val retrofitEpi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_EPI)
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
    private val retrofitGa by lazy {
        Retrofit.Builder()
            .baseUrl(URL_GA)
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
            .cache(Cache(AppModule.application.cacheDir,
                CACHE_SIZE_BYTES
            ))
            .build()
    }

    val gson by lazy {
        Gson()
    }

}
