package com.epigram.android.data.model

import org.joda.time.DateTime
import java.io.Serializable

data class Post(
    val id: String,
    val uuid: String,
    val title: String,
    val html: String,
    val image: String?,
    val tag: String?,
    val tags: Pair<List<String>?, List<String>?>,
    val authors: Triple<List<String>?, List<String>?, List<String>?>,
    val date: DateTime,
    val url : String

):Serializable{
    companion object{

        fun fromTemplate(template: PostTemplate): Post?{
            var x: String = template.html ?: ""
//            x = x.replace("<style>", "<!--")
//            x = x.replace("</style>", "-->")
            return Post(
                template.id,
                template.uuid,
                template.title,
                x,
                template.feature_image,
                template.primary_tag?.name?.toUpperCase(),
                Pair(template.tags?.map { tags -> tags.name }, template.tags?.map { tags -> tags.slug }),
                Triple(template.authors?.map { authors -> authors.name }, template.authors?.map { authors -> authors.slug }, template.authors?.map { authors -> authors.profile_image.orEmpty() }),
//                template.tags?.map { tags -> tags.slug },
                DateTime.parse(template.published_at),
                template.url
            )
        }
    }
}

data class PostTemplate(
    val id: String,
    val uuid: String,
    val title: String,
    val html: String?,
    val feature_image: String?,
    val primary_tag: Tags?,
    val tags: List<Tags>?,
    val authors: List<Authors>?,
    val published_at: String,
    val url: String
)

data class Wrapper<T>(
    val posts: List<T>
)

data class Tags(
    val name: String,
    val slug: String
)

data class Authors(
    val name: String,
    val slug: String,
    val profile_image: String?
)

data class SearchResult(
    val id: String,
    val title: String,
    val primary_author: Authors
)