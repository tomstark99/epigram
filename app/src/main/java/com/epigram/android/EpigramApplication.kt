package com.epigram.android

import android.app.Application
import com.epigram.android.arch.AppModule
import net.danlew.android.joda.JodaTimeAndroid

class EpigramApplication : Application() {
    override fun onCreate() {
        AppModule.application = this
        super.onCreate()
        JodaTimeAndroid.init(this)
    }
}