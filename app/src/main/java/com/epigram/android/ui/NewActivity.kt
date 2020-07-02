package com.epigram.android.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.epigram.android.R
import com.epigram.android.data.arch.PreferenceModule
import kotlinx.android.synthetic.main.activity_new.*
import kotlinx.android.synthetic.main.activity_welcome.*
import java.text.MessageFormat

class NewActivity : AppCompatActivity() {

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, NewActivity::class.java))
            activity.overridePendingTransition(0, 0)
        }
    }

    var counter = PreferenceModule.counter.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        new_touch.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
    }
}
