package com.example.epigram

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.epigram.data.Post
import kotlinx.android.synthetic.main.activity_article_view.*
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView


class ArticleActivity : AppCompatActivity() {

    var url: String = "https://epigram.org.uk/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_article_view)

        findViewById<View>(R.id.article_back).setOnClickListener { finish() }

        val share = findViewById<ImageView>(R.id.article_share)
        share.setOnClickListener { shareThis() }
    }

    private fun shareThis() {
        val shareIntent = ShareCompat.IntentBuilder.from(this@ArticleActivity)
            .setType("text/plain")
            .setText(url)
            .intent
        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(shareIntent, "share via"))
        }
    }


    override fun onStart() {
        super.onStart()

        val post = intent.getSerializableExtra(ARG_POST) as Post

        url = post.url

        val view = findViewById<TextView>(R.id.title)
        val typeFace = Typeface.createFromAsset(assets, "fonts/lora_regular.ttf")
        view.typeface = typeFace

        Glide.with(this).load(post.image)
                .placeholder(R.drawable.placeholder_background)
                .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(32))))
                .into(article_post_image)

        val htmlTextView: HtmlTextView = html_text
        htmlTextView.setHtml(post.html, HtmlHttpImageGetter(htmlTextView, null, true))
//      article_text.text = Html.fromHtml(post.html)

        article_post_title.text = post.title
        article_tag_text.text = post.tag
        article_post_date_alternate.text = post.date.toString("MMM d, yyyy")
    }

    companion object {

        const val ARG_POST = "post.object"

        fun start(context: Activity, post: Post, imageView: ImageView) {
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(ARG_POST, post)

            val options = ActivityOptions.makeSceneTransitionAnimation(context, imageView, "article_header")

            context.startActivity(intent, options.toBundle())

        }

        fun start(context: Activity, post: Post) {
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(ARG_POST, post)

            context.startActivity(intent)

        }
    }
}