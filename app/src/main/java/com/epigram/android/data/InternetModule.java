package com.epigram.android.data;

import com.epigram.android.BuildConfig;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class InternetModule {

    private static Retrofit retrofit;
    private static EpigramService epigramService;
    private static String URL = BuildConfig.URL;


    public static EpigramService getEpigramService(){


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);




        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        if(retrofit == null) {
            retrofit = new Retrofit.Builder().client(client)
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build();
        }
        if(epigramService == null) {
            epigramService = retrofit.create(EpigramService.class);
        }

        return epigramService;
    }

}