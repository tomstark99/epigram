package com.example.epigram.arch

import com.example.epigram.data.managers.PostManager

object DataModule {
    val postManager by lazy {
        PostManager()
    }
}