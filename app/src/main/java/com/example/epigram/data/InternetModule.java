package com.example.epigram.data;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InternetModule {

    private static Retrofit retrofit;
    private static EpigramService epigramService;


    public static EpigramService getEpigramService(){


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        if(retrofit == null) {
            retrofit = new Retrofit.Builder().client(client).baseUrl("https://epigram.ghost.io").addConverterFactory(GsonConverterFactory.create()).build();
        }
        if(epigramService == null) {
            epigramService = retrofit.create(EpigramService.class);
        }

        return epigramService;
    }

}
