package com.example.epigram.data.models

import com.example.epigram.data.templates.PostTemplate
import org.joda.time.DateTime
import java.io.Serializable

data class Post(
    val uuid: String,
    val title: String,
    val html: String,
    val image: String?,
    val date: DateTime
) : Serializable {
    companion object {

        fun fromTemplate(template: PostTemplate): Post? {
            var html: String = template.html ?: ""
            html = html.replace("<style>", "<!--")
            html = html.replace("</style>", "-->")

            return Post(
                template.uuid,
                template.title,
                html,
                template.image,
                DateTime.parse(template.published)
            )
        }
    }
}