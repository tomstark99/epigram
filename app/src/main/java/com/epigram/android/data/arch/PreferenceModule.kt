package com.epigram.android.data.arch

import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import androidx.appcompat.app.AppCompatDelegate.*

object PreferenceModule {

    val isFirstUse by lazy {
        rxSharedPrefs.getBoolean("is_first_use.bool", true)
    }

    val counter by lazy {
        rxSharedPrefs.getInteger("five_counter.int", 3)
    }

    val darkMode by lazy {
        rxSharedPrefs.getInteger("dark_mode.int", MODE_NIGHT_FOLLOW_SYSTEM)
    }

    val latestNotification by lazy {
        rxSharedPrefs.getString("latest_notification.string")
    }

    private val sharedPrefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(AppModule.application)
    }

    private val rxSharedPrefs by lazy {
        RxSharedPreferences.create(sharedPrefs)
    }
}