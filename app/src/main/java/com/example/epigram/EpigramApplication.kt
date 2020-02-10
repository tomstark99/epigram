package com.example.epigram

import android.app.Application
import com.example.epigram.arch.AppModule
import net.danlew.android.joda.JodaTimeAndroid

class EpigramApplication : Application() {
    override fun onCreate() {
        AppModule.application = this
        super.onCreate()
        JodaTimeAndroid.init(this)
    }
}