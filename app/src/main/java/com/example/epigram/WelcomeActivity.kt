package com.example.epigram

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, WelcomeActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val typeFace = Typeface.createFromAsset(getAssets(), "fonts/lora_bold.ttf")
        welcome_title.typeface = typeFace
        welcome_close.setOnClickListener{ finish() }
    }
}
