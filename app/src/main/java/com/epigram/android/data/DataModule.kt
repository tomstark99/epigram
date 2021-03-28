package com.epigram.android.data

import com.epigram.android.data.api.ApiModule
import com.epigram.android.data.managers.PostManagerImpl

object DataModule {

    val postManager by lazy {
        PostManagerImpl(ApiModule.apiServiceEpigram)
    }
}