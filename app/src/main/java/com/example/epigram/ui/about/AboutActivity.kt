package com.example.epigram.ui.about

import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.epigram.BuildConfig
import com.example.epigram.R
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_about.build
import kotlinx.android.synthetic.main.activity_settings.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val typeFace = Typeface.createFromAsset(getAssets(), "fonts/lora_bold.ttf")
        about_title.typeface = typeFace
        about_close.setOnClickListener{ finish() }
        version_number.text = getString(R.string.app_about, packageManager.getPackageInfo(packageName,0).versionName)
        build.text = getString(R.string.build, BuildConfig.VERSION_NAME, Build.VERSION.RELEASE, BuildConfig.BUILD_TIME.toString())

    }
}
