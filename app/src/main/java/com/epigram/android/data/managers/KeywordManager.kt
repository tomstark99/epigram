package com.epigram.android.data.managers

import io.reactivex.Single

interface KeywordManager {
    fun generateKeywordsFromLiked(): Single<List<String>>
    fun generateKeywordsFromTitle(title: String): Single<List<String>>
}