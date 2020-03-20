package com.epigram.android.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager.*
import com.epigram.android.R
import com.epigram.android.data.arch.PreferenceModule
import kotlinx.android.synthetic.main.activity_welcome.*
import java.text.MessageFormat

class WelcomeActivity : AppCompatActivity() {

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, WelcomeActivity::class.java))
        }
    }

    var counter = PreferenceModule.counter.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)
        val typeFace = Typeface.createFromAsset(getAssets(), "fonts/lora_bold.ttf")
        val str = resources.getString(R.string.welcome_left).toString()
        welcome_left.text = MessageFormat.format(str, counter)//resources.getQuantityString(R.plurals.welcome_left, counter, counter)
        welcome_title.typeface = typeFace
        welcome_close.setOnClickListener{ finish() }
    }
}
