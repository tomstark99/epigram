package com.example.epigram

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid

class EpigramApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
    }
}