package com.example.epigram.data

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
import com.example.epigram.ArticleActivity
import com.example.epigram.MainActivity
import com.example.epigram.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random

class NotificationService : FirebaseMessagingService() {

    companion object{
        var ID = Random.nextInt(5000)
    }

    private val postManager = PostManager()

    override fun onMessageReceived(p0: RemoteMessage) {
        //super.onMessageReceived(p0)

        if(!p0.data.get("id").isNullOrEmpty()) {
            postManager.getArticle(p0.data.get("id")!!)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe({ post ->
                    if(!post.image.isNullOrEmpty()) {
                        Glide.with(this)
                            .asBitmap()
                            .load(post.image)
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    createNotificationArticle(p0, post, resource)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {

                                }
                            })
                    }
                    else{
                        createNotificationArticle(p0, post, null)
                    }
                })
        }
        else if(!p0.data.get("titleNew").isNullOrEmpty()){
            createNotificationUpdate(p0)
        }
        else {
            createNotificationDraft(p0)
        }
    }



    fun createNotificationArticle(p0: RemoteMessage, post: Post, resource: Bitmap?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("article","New articles", importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
        val managerCompat = NotificationManagerCompat.from(this)
        val intent = ArticleActivity.makeIntent(this, post)
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification =  NotificationCompat.Builder(this, "article")
            .setContentTitle("New article published")
            .setContentText(p0.data.get("title"))
            .setSmallIcon(R.drawable.ic_clifton_icon)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(p0.data.get("title")))
            .apply { if(resource != null) setLargeIcon(resource) }
            .setAutoCancel(true)
            .setColor(getColor(R.color.colorPrimary))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        managerCompat.notify(ID++, notification)
    }

    fun createNotificationDraft(p0: RemoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("article_drafts","Article drafts", importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
        val pendingIntent = PendingIntent.getActivity(this, 1, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val managerCompat = NotificationManagerCompat.from(this)

        val notification =  NotificationCompat.Builder(this, "article_drafts")
            .setContentTitle("New article drafted")
            .setContentText(p0.data.get("title"))
            .setSmallIcon(R.drawable.ic_clifton_icon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(p0.data.get("title")))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(getColor(R.color.colorPrimary))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        managerCompat.notify(ID++, notification)
    }

    fun createNotificationUpdate(p0: RemoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("article_drafts","Article drafts", importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
        val pendingIntent = PendingIntent.getActivity(this, 1, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val managerCompat = NotificationManagerCompat.from(this)
        val contentText = if(p0.data.get("titleOld").isNullOrEmpty())
                                    "Article '" + p0.data.get("titleNew") + "' contents updated"
                               else "Article title change from '" + p0.data.get("titleOld") + "' to '" + p0.data.get("titleNew") + "'"

        val notification =  NotificationCompat.Builder(this, "article_drafts")
            .setContentTitle("Draft article updated")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_clifton_icon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(p0.data.get("title")))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(getColor(R.color.colorPrimary))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        managerCompat.notify(ID++, notification)
    }
}