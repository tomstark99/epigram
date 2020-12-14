package com.epigram.android.ui.article

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import com.epigram.android.ui.adapters.AdapterTag
import com.epigram.android.R
import com.epigram.android.data.arch.android.BaseActivity
import com.epigram.android.data.arch.utils.LoadNextPage
import com.epigram.android.data.arch.utils.SnapHelperOne
import com.epigram.android.data.arch.utils.Utils
import com.epigram.android.data.model.Post
import com.epigram.android.ui.adapters.AdapterAuthorTag
import com.epigram.android.ui.adapters.AdapterBreaking
import kotlinx.android.synthetic.main.activity_article_view.*
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView
import java.util.*


class ArticleActivity : BaseActivity<ArticleMvp.Presenter>(), ArticleMvp.View, LoadNextPage {

    var url: String = "https://epigram.org.uk/"
    private var recyclerView: RecyclerView? = null
    lateinit var post: Post

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

        presenter = ArticlePresenter(this)

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

        post = intent.getSerializableExtra(ARG_POST) as Post

        recyclerView = findViewById(R.id.recycler_view_tag)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = AdapterTag(post.tags)

        val layout = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_view_author!!.layoutManager = layout
        recycler_view_author!!.itemAnimator = DefaultItemAnimator()
        recycler_view_author!!.adapter = AdapterAuthorTag(post.authors)

        val snapHelper = SnapHelperOne()
        recycler_related!!.onFlingListener = null
        snapHelper.attachToRecyclerView(recycler_related!!)
        recycler_related!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val slugs = post.tags.second.orEmpty().toMutableList()
        slugs.removeAll(Arrays.asList("featured-top", "carousel", "one-sidebar", "weeklytop", "no-sidebar"))
        if (slugs.isNotEmpty()){ presenter.load(slugs[0]) }
        else {
            related.visibility = View.GONE
            recycler_related.visibility = View.GONE
        }

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
//        val formattedHtml = HtmlFormatter.formatHtml(HtmlFormatterBuilder().setHtml(post.html).setImageGetter(HtmlHttpImageGetter(htmlTextView, null, true)))
//        htmlTextView.text = formattedHtml
//      article_text.text = Html.fromHtml(post.html)
        article_post_title.text = post.title
        //article_tag_text.text = post.tag
        article_post_date_alternate.text = Utils.dateText(post.date)//post.date.toString("MMM d, yyyy")
    }

    override fun onPostSuccess(posts: List<Post>) {
        val p = posts.toMutableList()
        p.remove(post)
        if(p.isNotEmpty()){ recycler_related.adapter = AdapterBreaking(this, p, this) }
        else {
            related.visibility = View.GONE
            recycler_related.visibility = View.GONE
        }
    }

    override fun onPostError() {

    }

    override fun setClickables() {

    }

    override fun bottomReached() {
        return
    }

    override fun onPostClicked(clicked: Post, titleImage: ImageView?) {
        if (titleImage != null) {
            start(this, clicked, titleImage)
        } else {
            start(this, clicked)
        }
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