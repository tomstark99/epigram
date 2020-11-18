package com.epigram.android.ui.settings

import android.app.Dialog
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup.*
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate.*
import com.epigram.android.BuildConfig
import com.epigram.android.R
import com.epigram.android.data.arch.PreferenceModule
import com.epigram.android.data.arch.android.BaseActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity<SettingsMvp.Presenter>(), SettingsMvp.View {

    var clicked = false
    lateinit var th_m: Dialog
    lateinit var th_no: Button
    lateinit var th_rg: RadioGroup
    lateinit var th_light: RadioButton
    lateinit var th_dark: RadioButton
    lateinit var th_followDevice: RadioButton
    lateinit var th_current: RadioButton

    lateinit var ly_m: Dialog
    lateinit var ly_no: Button
    lateinit var ly_rg: RadioGroup
    lateinit var ly_default: RadioButton
    lateinit var ly_compact: RadioButton
    lateinit var ly_current: RadioButton

    enum class Theme(val id: Int, @StringRes val theme: Int, @IdRes val rb: Int){
        LIGHT(0, R.string.light, R.id.light),
        DARK(1, R.string.dark, R.id.dark),
        FOLLOW_SYSTEM(2, R.string.follow_device, R.id.follow_device)
    }

    enum class Layout(val id: Int, @StringRes val layout: Int, @IdRes val rb: Int){
        DEFAULT(0, R.string.default_layout, R.id.default_layout),
        COMPACT(1, R.string.compact_layout, R.id.compact_layout)
    }

    private var map = mutableMapOf<Int, Int>(MODE_NIGHT_NO to 0,
                                            MODE_NIGHT_YES to 1,
                                            MODE_NIGHT_FOLLOW_SYSTEM to 2)

    private var th_buttonMap = mutableMapOf<RadioButton, Int>()
    private var ly_buttonMap = mutableMapOf<RadioButton, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SettingsPresenter(this)
        presenter.onCreate()
    }

    override fun load() {
        setContentView(R.layout.activity_settings)
        th_m = Dialog(this, R.style.SubmitTheme)
        th_m.requestWindowFeature(Window.FEATURE_NO_TITLE)
        th_m.setCancelable(true)
        th_m.setContentView(R.layout.element_dialog_theme)
        th_m.window!!.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        ly_m = Dialog(this, R.style.SubmitTheme)
        ly_m.requestWindowFeature(Window.FEATURE_NO_TITLE)
        ly_m.setCancelable(true)
        ly_m.setContentView(R.layout.element_dialog_layout)
        ly_m.window!!.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        th_no = th_m.findViewById(R.id.btNegative) as Button
        th_rg = th_m.findViewById(R.id.theme_group) as RadioGroup
        th_light = th_m.findViewById(R.id.light) as RadioButton
        th_dark = th_m.findViewById(R.id.dark) as RadioButton
        th_followDevice = th_m.findViewById(R.id.follow_device) as RadioButton
        th_current = th_m.findViewById(Theme.values()[map[getDefaultNightMode()]!!].rb) as RadioButton

        ly_no = ly_m.findViewById(R.id.btNegative) as Button
        ly_rg = ly_m.findViewById(R.id.layout_group) as RadioGroup
        ly_default = ly_m.findViewById(R.id.default_layout) as RadioButton
        ly_compact = ly_m.findViewById(R.id.compact_layout) as RadioButton
        ly_current = ly_m.findViewById(Layout.values()[PreferenceModule.layoutMode.get()].rb) as RadioButton

        th_buttonMap[th_light] = MODE_NIGHT_NO
        th_buttonMap[th_dark] = MODE_NIGHT_YES
        th_buttonMap[th_followDevice] = MODE_NIGHT_FOLLOW_SYSTEM

        ly_buttonMap[ly_default] = 0
        ly_buttonMap[ly_compact] = 1

        val typeFace = Typeface.createFromAsset(getAssets(), "fonts/lora_bold.ttf")
        settings_title.typeface = typeFace
        build.text = getString(R.string.build, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString(), BuildConfig.BUILD_TIME.toString())
        theme_request.setText(Theme.values()[map[PreferenceModule.darkMode.get()]!!].theme)
        layout_request.setText(Layout.values()[PreferenceModule.layoutMode.get()].layout)
    }

    override fun setClickables() {
        settings_back.setOnClickListener{ finish() }
        theme_setting.setOnClickListener { th_m.show() }
        layout_setting.setOnClickListener { ly_m.show() }
        build_button.setOnClickListener {  }

        th_no.setOnClickListener { th_m.cancel() }
        th_current.isChecked = true
        th_current.setOnClickListener { th_m.cancel() }
        th_rg.setOnCheckedChangeListener { group, i ->
            var c = group.findViewById(i) as RadioButton
            if(c.isChecked) {
                setDefaultNightMode(th_buttonMap[c]!!)
                presenter.setTheme(th_buttonMap[c]!!)
                theme_request.setText(Theme.values()[map[PreferenceModule.darkMode.get()]!!].theme)
                th_m.cancel()
            }
        }

        ly_no.setOnClickListener { ly_m.cancel() }
        ly_current.isChecked = true
        ly_current.setOnClickListener { ly_m.cancel() }
        ly_rg.setOnCheckedChangeListener { group, i ->
            var c = group.findViewById(i) as RadioButton
            if(c.isChecked) {
                presenter.setLayout(ly_buttonMap[c]!!)
                layout_request.setText(Layout.values()[PreferenceModule.layoutMode.get()].layout)
                ly_m.cancel()
            }
        }
    }
}
