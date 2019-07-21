package com.example.epigram.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.epigram.ui.article.ArticleActivity
import com.example.epigram.ui.main.MainActivity
import com.example.epigram.R
import com.example.epigram.data.managers.PostManager
import com.example.epigram.data.models.Post
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import kotlin.random.Random

class NotificationService : FirebaseMessagingService() {

    //These should be injected via an AppComponentFactory
    private val notificationManager = NotificationManagerCompat.from(this)
    private val postManager = PostManager()

    private val disposable = CompositeDisposable()

    private val mainActivityIntent by lazy {
        val intent = Intent(this, MainActivity::class.java)
        PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onMessageReceived(message: RemoteMessage) {

        val id = message.data[KEY_ID]

        if (!id.isNullOrEmpty()) {
            postManager.getArticle(id)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe { post ->
                    if (post.image.isNullOrEmpty()) {
                        createNotificationArticle(message, post, null)
                    } else {
                        Glide.with(this)
                            .asBitmap()
                            .load(post.image)
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    createNotificationArticle(message, post, resource)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {

                                }
                            })
                    }
                }
                .addTo(disposable)
        } else if (!message.data[KEY_NEW_TITLE].isNullOrEmpty()) {
            createNotificationUpdate(message)
        } else {
            createNotificationDraft(message)
        }
    }

    private fun createNotificationArticle(message: RemoteMessage, post: Post, resource: Bitmap?) {
        val title = message.data[KEY_TITLE]

        if (title == null) {
            Timber.e("No title for create notification")
            return
        }

        val intent = ArticleActivity.makeIntent(this, post)
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        sendNotification(
            getString(R.string.notification_channel_article_created_title),
            title,
            getString(R.string.notification_channel_article_created_id),
            getString(R.string.notification_channel_article_created_name),
            pendingIntent,
            NotificationCompat.BigTextStyle().bigText(title)
        ) {
            if (resource != null) it.setLargeIcon(resource)
        }
    }

    private fun createNotificationDraft(message: RemoteMessage) {
        val title = message.data[KEY_TITLE]

        if (title == null) {
            Timber.e("No title for draft notification")
            return
        }

        val style = NotificationCompat.BigTextStyle().bigText(title)

        sendNotification(
            getString(R.string.notification_channel_article_drafts_title),
            title,
            getString(R.string.notification_channel_article_drafts_id),
            getString(R.string.notification_channel_article_drafts_name),
            mainActivityIntent,
            style
        )
    }

    private fun createNotificationUpdate(message: RemoteMessage) {

        val contentText = if (message.data[KEY_OLD_TITLE].isNullOrEmpty()) {
            getString(R.string.notification_channel_article_updated_body_only, message.data[KEY_NEW_TITLE])
        } else {
            getString(R.string.notification_channel_article_updated_both, message.data[KEY_OLD_TITLE], message.data[KEY_NEW_TITLE])
        }

        val style = NotificationCompat.BigTextStyle().bigText(message.data[KEY_TITLE])

        sendNotification(
            getString(R.string.notification_channel_article_updated_title),
            contentText,
            getString(R.string.notification_channel_article_updated_id),
            getString(R.string.notification_channel_article_updated_name),
            mainActivityIntent,
            style
        )
    }

    private fun sendNotification(
        title: String,
        body: String,
        channelId: String,
        channelName: String,
        pendingIntent: PendingIntent,
        style: NotificationCompat.Style? = null,
        transform: ((NotificationCompat.Builder) -> Unit)? = null
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_clifton_icon)
            .setStyle(style)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(getColor(R.color.colorPrimary))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .let {
                transform?.invoke(it)
                it
            }
            .build()

        notificationManager.notify(ID++, notification)
    }

    companion object {
        private var ID = Random.nextInt(5000)

        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_NEW_TITLE = "titleNew"
        private const val KEY_OLD_TITLE = "titleOld"
    }
}