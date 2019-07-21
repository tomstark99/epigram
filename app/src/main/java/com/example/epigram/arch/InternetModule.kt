package com.example.epigram.arch

import com.example.epigram.BuildConfig
import com.example.epigram.data.api.EpigramService
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit

object InternetModule {

    val epigramService by lazy {
        retrofit.create(EpigramService::class.java)
    }

    private val retrofit by lazy {
        Retrofit.Builder().client(okhttp)
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    private val okhttp by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder()
            .addNetworkInterceptor {chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("x-api-key", BuildConfig.API_KEY)
                    .build()

                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()
    }
}
