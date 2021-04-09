package com.epigram.android.data.managers

import io.reactivex.Single

interface KeywordManager {
    fun generateKeywords(): Single<List<String>>
}