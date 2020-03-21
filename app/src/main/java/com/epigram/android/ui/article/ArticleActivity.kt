package com.epigram.android.ui.article

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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.epigram.android.ui.adapters.MyAdapterTag
import com.epigram.android.R
import com.epigram.android.data.model.Post
import kotlinx.android.synthetic.main.activity_article_view.*
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView


class ArticleActivity : AppCompatActivity() {

    var url: String = "https://epigram.org.uk/"
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_article_view)

        findViewById<View>(R.id.article_back).setOnClickListener {
            if(!intent.getBooleanExtra(FROM_APP, false)){
                val intent = NavUtils.getParentActivityIntent(this)
                NavUtils.navigateUpTo(this, intent!!)
            }
            finishAfterTransition()
        }

        val share = findViewById<ImageView>(R.id.article_share)
        share.setOnClickListener { shareThis() }

        article_scroll.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if(scrollY == 0 && oldScrollY != 0) {
                appbar.elevation = 0f
            }
            if(oldScrollY == 0 && scrollY != 0) {
                appbar.elevation = resources.getDimension(R.dimen.appbar_elevation)
            }
        }

        findViewById<TextView>(R.id.title).setOnClickListener{ findViewById<NestedScrollView>(
            R.id.article_scroll
        ).smoothScrollTo(0,0) }

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

        recyclerView = findViewById(R.id.recycler_view_tag)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter =
            MyAdapterTag(post.tags!!.toMutableList())

        url = post.url

        val view = findViewById<TextView>(R.id.title)
        val typeFace = Typeface.createFromAsset(assets, "fonts/lora_bold.ttf")
        view.typeface = typeFace

        Glide.with(this).load(post.image)
                .placeholder(R.drawable.placeholder_background)
                .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(40))))
                .into(article_post_image)

        val htmlTextView: HtmlTextView = html_text
        htmlTextView.setHtml(post.html, HtmlHttpImageGetter(htmlTextView, null, true))
//      article_text.text = Html.fromHtml(post.html)
        article_post_title.text = post.title
        //article_tag_text.text = post.tag
        article_post_date_alternate.text = post.date.toString("MMM d, yyyy")
    }

    companion object {

        const val ARG_POST = "post.object"
        const val FROM_APP = "boolean"

        fun start(context: Activity, post: Post, imageView: ImageView) {
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(ARG_POST, post)
            intent.putExtra(FROM_APP, true)

            val options = ActivityOptions.makeSceneTransitionAnimation(context, imageView, "article_header")

            context.startActivity(intent, options.toBundle())

        }

        fun start(context: Activity, post: Post) {
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(ARG_POST, post)
            intent.putExtra(FROM_APP, true)

            context.startActivity(intent)

        }

        fun makeIntent(context: Context, post: Post): Intent{
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(ARG_POST, post)
            intent.putExtra(FROM_APP, false)
            return intent
        }
    }
}