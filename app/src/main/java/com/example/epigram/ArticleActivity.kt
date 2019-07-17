package com.example.epigram

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ShareCompat
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.epigram.data.NotificationService
import com.example.epigram.data.Post
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.android.synthetic.main.activity_article_view.*
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView


class ArticleActivity : AppCompatActivity() {

    var url: String = "https://epigram.org.uk/"

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

        findViewById<TextView>(R.id.title).setOnClickListener{ findViewById<NestedScrollView>(R.id.article_scroll).smoothScrollTo(0,0) }
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


    fun createNotificationArticle(post: Post, bitmap: Bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel("article","New articles", importance)
            val notificationManager = getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
        val managerCompat = NotificationManagerCompat.from(this)
        val intent = ArticleActivity.makeIntent(this, post)
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

//        val placeholder =


        val notification =  NotificationCompat.Builder(this, "article")
            .setContentTitle("New article published")
            .setContentText(post.title)
            .setSmallIcon(R.drawable.ic_clifton_icon)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(post.title))
            .setLargeIcon(bitmap)
            .setAutoCancel(true)
            .setColor(getColor(R.color.colorPrimary))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        managerCompat.notify(NotificationService.ID++, notification)
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