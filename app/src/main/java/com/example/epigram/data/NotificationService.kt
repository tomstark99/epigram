package com.example.epigram.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.epigram.ArticleActivity
import com.example.epigram.MainActivity
import com.example.epigram.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NotificationService : FirebaseMessagingService() {

    private val postManager = PostManager()

    override fun onMessageReceived(p0: RemoteMessage) {
        //super.onMessageReceived(p0)

        if(!p0.data.get("id").isNullOrEmpty()) {
            postManager.getArticle(p0.data.get("id")!!)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe({ post ->
                    createNotificationArticle(p0, post)
                })
        }
        else{
            createNotificationDraft(p0)
        }
    }

    fun createNotificationArticle(p0: RemoteMessage, post: Post){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel("article","New articles", importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        val managerCompat = NotificationManagerCompat.from(this)

        val intent = ArticleActivity.makeIntent(this, post)
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, "article")
            .setContentTitle("New article published")
            .setContentText(p0.data.get("title"))
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(p0.data.get("title")))
            .setAutoCancel(true)
            .setColor(getColor(R.color.colorPrimary))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        managerCompat.notify(1, notification)

    }

    fun createNotificationDraft(p0: RemoteMessage){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel("article_drafts","Article drafts", importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        val pendingIntent = PendingIntent.getActivity(this, 1, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

        val managerCompat = NotificationManagerCompat.from(this)

        val notification = NotificationCompat.Builder(this, "article_drafts")
            .setContentTitle("New article drafted")
            .setContentText(p0.data.get("title"))
            .setSmallIcon(R.drawable.ic_clifton_icon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(p0.data.get("title")))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(getColor(R.color.colorPrimary))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        managerCompat.notify(1, notification)

    }
}