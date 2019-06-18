package com.example.epigram.data

import org.joda.time.DateTime
import java.io.Serializable

data class Post(
    val id: String,
    val uuid: String,
    val title: String,
    val html: String,
    val image: String,
    val tag: String,
    val date: DateTime

):Serializable{
    companion object{fun fromTemplate(template: PostTemplate): Post?{
        //System.out.println(template.id)
        //if(template.id.equals("5cd2b5b0f7ff1100c0a7eab8")) {
            //System.out.println(template.html)
            //System.out.println("==================")
            var x: String = template.html
            x = x.replace("<style>", "<!--")
            x = x.replace("</style>", "-->")
            //System.out.println(x)
        //}
        //template.html.replace("<style>.+<\\/style>", "")
        return Post(template.id,template.uuid,template.title,x,template.feature_image, template.primary_tag.name.toUpperCase(), DateTime.parse(template.published_at))
    }}
}

data class PostTemplate(
    val id: String,
    val uuid: String,
    val title: String,
    val html: String,
    val feature_image: String,
    val primary_tag: Tags,
    val published_at: String
)

data class postTemplateWrapper(
    val posts: List<PostTemplate>
)

data class Tags(
    val id: String,
    val name: String
)