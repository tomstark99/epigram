package com.example.epigram;

import android.app.Application;
import net.danlew.android.joda.JodaTimeAndroid;

public class EpigramApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}