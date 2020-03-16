package com.epigram.android.ui.settings

import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.*
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.*
import com.epigram.android.BuildConfig
import com.epigram.android.R
import com.epigram.android.arch.PreferenceModule
import com.epigram.android.arch.android.BaseActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.element_theme_dialog.*

class SettingsActivity : BaseActivity<SettingsMvp.Presenter>(), SettingsMvp.View {

    var clicked = false
    lateinit var m: Dialog
    lateinit var no: Button
    lateinit var rg: RadioGroup
    lateinit var light: RadioButton
    lateinit var dark: RadioButton
    lateinit var followDevice: RadioButton
    lateinit var current: RadioButton

    enum class Theme(val id: Int, @StringRes val theme: Int, @IdRes val rb: Int){
        LIGHT(0, R.string.light, R.id.light),
        DARK(1, R.string.dark, R.id.dark),
        FOLLOW_SYSTEM(2, R.string.follow_device, R.id.follow_device)
    }

    private var map = mutableMapOf<Int, Int>(MODE_NIGHT_NO to 0,
                                            MODE_NIGHT_YES to 1,
                                            MODE_NIGHT_FOLLOW_SYSTEM to 2)

    private var buttonMap = mutableMapOf<RadioButton, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SettingsPresenter(this)
        presenter.onCreate()
    }

    override fun load() {
        setContentView(R.layout.activity_settings)
        m = Dialog(this, R.style.SubmitTheme)
        m.requestWindowFeature(Window.FEATURE_NO_TITLE)
        m.setCancelable(true)
        m.setContentView(R.layout.element_theme_dialog)
        m.window!!.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        no = m.findViewById(R.id.btNegative) as Button
        rg = m.findViewById(R.id.theme_group) as RadioGroup
        light = m.findViewById(R.id.light) as RadioButton
        dark = m.findViewById(R.id.dark) as RadioButton
        followDevice = m.findViewById(R.id.follow_device) as RadioButton
        current = m.findViewById(Theme.values()[map[getDefaultNightMode()]!!].rb) as RadioButton

        buttonMap[light] = MODE_NIGHT_NO
        buttonMap[dark] = MODE_NIGHT_YES
        buttonMap[followDevice] = MODE_NIGHT_FOLLOW_SYSTEM

        val typeFace = Typeface.createFromAsset(getAssets(), "fonts/lora_bold.ttf")
        settings_title.typeface = typeFace
        build.text = getString(R.string.build, BuildConfig.VERSION_NAME, Build.VERSION.RELEASE, BuildConfig.BUILD_TIME.toString())
        theme_request.setText(Theme.values()[map[PreferenceModule.darkMode.get()]!!].theme)
    }

    override fun setClickables() {
        settings_back.setOnClickListener{ finish() }
        theme_setting.setOnClickListener { m.show() }
        build_button.setOnClickListener {  }
        no.setOnClickListener { m.cancel() }
        current.isChecked = true
        current.setOnClickListener { m.cancel() }
        rg.setOnCheckedChangeListener { group, i ->
            var c = group.findViewById(i) as RadioButton
            if(c.isChecked) {
                setDefaultNightMode(buttonMap[c]!!)
                presenter.setTheme(buttonMap[c]!!)
                theme_request.setText(Theme.values()[map[PreferenceModule.darkMode.get()]!!].theme)
                m.cancel()
            }
        }
    }
}
