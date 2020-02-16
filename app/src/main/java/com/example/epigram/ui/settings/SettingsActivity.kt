package com.example.epigram.ui.settings

import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.epigram.BuildConfig
import com.example.epigram.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val typeFace = Typeface.createFromAsset(getAssets(), "fonts/lora_bold.ttf")
        settings_title.typeface = typeFace
        build.text = getString(R.string.build, BuildConfig.VERSION_NAME, Build.VERSION.RELEASE, BuildConfig.BUILD_TIME.toString())
        settings_back.setOnClickListener{ finish() }
        settings_dark_card.setOnClickListener{ dark_mode_tick.isChecked = !dark_mode_tick.isChecked}
    }

    fun switch(bool: Boolean): Boolean{
        return !bool
    }
}
