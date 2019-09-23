package com.example.epigram

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val typeFace = Typeface.createFromAsset(getAssets(), "fonts/lora_bold.ttf")
        about_title.typeface = typeFace
        about_close.setOnClickListener{ finish() }
    }
}
