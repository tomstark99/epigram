package com.example.epigram.data.models

import com.example.epigram.data.templates.PostTemplate
import org.joda.time.DateTime
import java.io.Serializable

data class Post(
    val id: String,
    val uuid: String,
    val title: String,
    val html: String,
    val image: String?,
    val tag: String?,
    val date: DateTime,
    val url : String

): Serializable {
    companion object{

        fun fromTemplate(template: PostTemplate): Post?{
            var html: String = template.html ?: ""
            html = html.replace("<style>", "<!--")
            html = html.replace("</style>", "-->")
            return Post(template.id,template.uuid,template.title,html,template.feature_image, template.primary_tag?.name?.toUpperCase(), DateTime.parse(template.published_at),template.url)
        }
    }
}