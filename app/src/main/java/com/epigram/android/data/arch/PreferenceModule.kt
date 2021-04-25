package com.epigram.android.data.arch

import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import androidx.appcompat.app.AppCompatDelegate.*

object PreferenceModule {

    val isFirstUse by lazy {
        rxSharedPrefs.getBoolean("is_first_use.bool", true)
    }

    val seenNew by lazy {
        rxSharedPrefs.getBoolean("seen_new.bool",false)
    }

    val counter by lazy {
        rxSharedPrefs.getInteger("counter.int", -1)
    }

    val darkMode by lazy {
        rxSharedPrefs.getInteger("dark_mode.int", MODE_NIGHT_FOLLOW_SYSTEM)
    }

    val layoutMode by lazy {
        rxSharedPrefs.getInteger("layout.int", 1)
    }

    val advancedForYou by lazy {
        rxSharedPrefs.getInteger("foryou.int", 1)
    }

    val latestNotification by lazy {
        rxSharedPrefs.getString("latest_notification.string")
    }

    val savedPosts by lazy {
        rxSharedPrefs.getStringSet("saved.list", mutableSetOf())
    }

    val likedPosts by lazy {
        rxSharedPrefs.getStringSet("liked.list", mutableSetOf())
    }

    val likedTags by lazy {
        rxSharedPrefs.getStringSet("tags.list", mutableSetOf("news","the-croft"))
    }
    val likedAuthors by lazy {
        rxSharedPrefs.getStringSet("authors.list", mutableSetOf("epigram-news"))
    }

    val keywords by lazy {
        rxSharedPrefs.getStringSet("keywords.list", mutableSetOf())
    }

    private val sharedPrefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(AppModule.application)
    }

    private val rxSharedPrefs by lazy {
        RxSharedPreferences.create(sharedPrefs)
    }
}