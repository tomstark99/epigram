package com.example.epigram.arch

import com.example.epigram.data.api.EpigramService
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit

object InternetModule {

    private var retrofit: Retrofit? = null
    private var epigramService: EpigramService? = null


    fun getEpigramService(): EpigramService {


        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY


        val client = OkHttpClient.Builder().addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder().client(client)
                .baseUrl("https://epigram.ghost.io")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
        }
        if (epigramService == null) {
            epigramService = retrofit!!.create(EpigramService::class.java)
        }

        return epigramService
    }

}
