package com.example.epigram.ui.article

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.app.ShareCompat
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.epigram.R
import com.example.epigram.data.models.Post
import kotlinx.android.synthetic.main.activity_article_view.*
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView

class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_article_view)

        article_back.setOnClickListener {
            if (!intent.getBooleanExtra(ARG_FROM_APP, false)) {
                val intent = NavUtils.getParentActivityIntent(this)
                NavUtils.navigateUpTo(this, intent!!)
            }
            finishAfterTransition()
        }

        val share = findViewById<ImageView>(R.id.article_share)
        share.setOnClickListener { shareThis() }

        article_title.setOnClickListener { article_scroll.smoothScrollTo(0, 0) }
    }

    private fun shareThis() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setText((intent.getSerializableExtra(ARG_POST) as Post).url)
            .intent
        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(shareIntent, "share via"))
        }
    }

    override fun onStart() {
        super.onStart()

        val post = intent.getSerializableExtra(ARG_POST) as Post

        article_title.typeface = Typeface.createFromAsset(assets, "fonts/lora_regular.ttf")

        Glide.with(this).load(post.image)
            .placeholder(R.drawable.placeholder_background)
            .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(32))))
            .into(article_post_image)

        val htmlTextView: HtmlTextView = article_html_text
        htmlTextView.setHtml(post.html, HtmlHttpImageGetter(htmlTextView, null, true))
        article_post_title.text = post.title
        article_tag_text.text = post.tag
        article_post_date_alternate.text = post.date.toString("MMM d, yyyy")
    }

    companion object {

        const val ARG_POST = "post.object"
        const val ARG_FROM_APP = "from_app.boolean"

        fun start(context: Activity, post: Post, imageView: ImageView? = null) {
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(ARG_POST, post)
            intent.putExtra(ARG_FROM_APP, true)

            val options = if (imageView == null) {
                Bundle()
            } else {
                ActivityOptions.makeSceneTransitionAnimation(context, imageView, "article_header").toBundle()
            }

            context.startActivity(intent, options)
        }

        fun makeIntent(context: Context, post: Post): Intent {
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(ARG_POST, post)
            intent.putExtra(ARG_FROM_APP, false)
            return intent
        }
    }
}