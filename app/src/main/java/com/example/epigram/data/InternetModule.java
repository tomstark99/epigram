package com.example.epigram.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InternetModule {

    private static Retrofit retrofit;
    private static EpigramService epigramService;


    public static EpigramService getEpigramService(){

        if(retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl("https://epigram.ghost.io").addConverterFactory(GsonConverterFactory.create()).build();
        }
        if(epigramService == null) {
            epigramService = retrofit.create(EpigramService.class);
        }

        return epigramService;
    }

}
