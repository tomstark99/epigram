package com.epigram.android.ui.settings

import android.app.Dialog
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
    lateinit var light: RadioButton
    lateinit var dark: RadioButton
    //lateinit var followDevice: RadioButton

    enum class Theme(val id: Int, @StringRes val theme: Int){
        LIGHT(0, R.string.light),
        DARK(1, R.string.dark),
        //FOLLOW_SYSTEM(2, R.string.follow_device)
    }

    private var map = mutableMapOf<Int, Int>(MODE_NIGHT_NO to 0,
                                            MODE_NIGHT_YES to 1)//,
                                            //MODE_NIGHT_FOLLOW_SYSTEM to 2)

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
        light = m.findViewById(R.id.light) as RadioButton
        dark = m.findViewById(R.id.dark) as RadioButton
        //followDevice = m.findViewById(R.id.follow_device) as RadioButton

        val typeFace = Typeface.createFromAsset(getAssets(), "fonts/lora_bold.ttf")
        settings_title.typeface = typeFace
        build.text = getString(R.string.build, BuildConfig.VERSION_NAME, Build.VERSION.RELEASE, BuildConfig.BUILD_TIME.toString())
        theme_request.setText(Theme.values()[map[PreferenceModule.darkMode.get()]!!].theme)
    }

    override fun setClickables() {
        settings_back.setOnClickListener{ finish() }
        theme_setting.setOnClickListener { m.show() }
        no.setOnClickListener { m.cancel() }
        if(getDefaultNightMode() == MODE_NIGHT_YES) dark.isChecked = true else light.isChecked = true
        light.setOnClickListener {
            m.cancel()
            if(getDefaultNightMode() != MODE_NIGHT_NO) {
                setDefaultNightMode(MODE_NIGHT_NO)
                presenter.setTheme(MODE_NIGHT_NO)
            }
        }
        dark.setOnClickListener {
            m.cancel()
            if(getDefaultNightMode() != MODE_NIGHT_YES){
                setDefaultNightMode(MODE_NIGHT_YES)
                presenter.setTheme(MODE_NIGHT_YES)
            }
        }
//        followDevice.setOnClickListener {
//            m.cancel()
//            setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
//            presenter.setTheme(MODE_NIGHT_FOLLOW_SYSTEM)
//        }

    }

    fun switch(bool: Boolean): Boolean{
        return !bool
    }
}
