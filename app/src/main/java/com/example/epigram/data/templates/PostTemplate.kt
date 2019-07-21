package com.example.epigram.data.templates

data class PostTemplate(
    val uuid: String,
    val title: String,
    val html: String,
    val image: String?,
    val published: String
)