package com.epigram.android.arch

import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences

object PreferenceModule {

    val isFirstUse by lazy {
        rxSharedPrefs.getBoolean("is_first_use.bool", true)
    }

    val counter by lazy {
        rxSharedPrefs.getInteger("five_counter.int", 3)
    }

    val darkMode by lazy {
        rxSharedPrefs.getBoolean("dark_mode.bool", false)
    }

    private val sharedPrefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(AppModule.application)
    }

    private val rxSharedPrefs by lazy {
        RxSharedPreferences.create(sharedPrefs)
    }
}