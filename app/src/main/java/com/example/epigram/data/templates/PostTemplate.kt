package com.example.epigram.data.templates


data class PostTemplate(
    val id: String,
    val uuid: String,
    val title: String,
    val html: String?,
    val feature_image: String?,
    val primary_tag: Tags?,
    val published_at: String,
    val url: String
)

data class Wrapper<T>(
    val posts: List<T>
)

data class Tags(
    val id: String,
    val name: String
)

data class Authors(
    val name: String
)

data class SearchResult(
    val id: String,
    val title: String,
    val primary_author: Authors
)