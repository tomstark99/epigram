package com.epigram.android.data

import org.joda.time.DateTime
import java.io.Serializable

data class Post(
    val id: String,
    val uuid: String,
    val title: String,
    val html: String,
    val image: String?,
    val tag: String?,
    val tags: List<String>?,
    val date: DateTime,
    val url : String

):Serializable{
    companion object{

        fun fromTemplate(template: PostTemplate): Post?{
        //System.out.println(template.id)
        //if(template.id.equals("5cd2b5b0f7ff1100c0a7eab8")) {
            //System.out.println(template.html)
            //System.out.println("==================")
            var x: String = template.html ?: ""
            x = x.replace("<style>", "<!--")
            x = x.replace("</style>", "-->")
            //System.out.println(x)
        //}
        //template.html.replace("<style>.+<\\/style>", "")
        return Post(template.id,template.uuid,template.title,x,template.feature_image, template.primary_tag?.name?.toUpperCase(), template.tags?.map { tags -> tags.name }, DateTime.parse(template.published_at),template.url)
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