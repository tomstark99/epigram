package com.epigram.android.data

import com.epigram.android.data.api.ApiModule
import com.epigram.android.data.managers.PostManagerImpl
import com.epigram.android.data.managers.ViewManagerImpl

object DataModule {

    val postManager by lazy {
        PostManagerImpl(ApiModule.apiServiceEpigram)
    }

    val gaManager by lazy {
        ViewManagerImpl(ApiModule.apiServiceGa, ApiModule.apiServiceEpigram)
    }
}