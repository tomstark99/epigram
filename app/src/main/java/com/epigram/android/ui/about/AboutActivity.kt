package com.epigram.android.ui.about

import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.epigram.android.BuildConfig
import com.epigram.android.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val typeFace = Typeface.createFromAsset(getAssets(), "fonts/lora_bold.ttf")
        about_title.typeface = typeFace
        about_close.setOnClickListener{ finish() }
        version_number.text = getString(R.string.app_about, packageManager.getPackageInfo(packageName,0).versionName)
    }
}
