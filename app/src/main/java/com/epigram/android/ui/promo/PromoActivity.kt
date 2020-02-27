package com.epigram.android.ui.promo

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager.*
import com.epigram.android.R
import com.epigram.android.arch.PreferenceModule
import kotlinx.android.synthetic.main.activity_welcome.*
import java.text.MessageFormat

class PromoActivity : AppCompatActivity() {

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, PromoActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo)
        window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)
        val typeFace = Typeface.createFromAsset(getAssets(), "fonts/lora_bold.ttf")
        welcome_title.typeface = typeFace
        welcome_close.setOnClickListener{ finish() }
    }
}
