package com.example.epigram.ui.main

import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.epigram.R
import com.example.epigram.ui.search.SearchActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        main_view_pager.adapter = sectionsPagerAdapter
        main_tabs.setupWithViewPager(main_view_pager)

        main_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                sectionsPagerAdapter.fragmentReselected(tab.position)
            }
        })

        val typeFace = Typeface.createFromAsset(assets, "fonts/lora_regular.ttf")
        main_title.typeface = typeFace

        main_search_button.setOnClickListener { SearchActivity.start(this@MainActivity) }

        FirebaseMessaging.getInstance().subscribeToTopic("new_article")
            .addOnCompleteListener { }

        FirebaseMessaging.getInstance().subscribeToTopic("new_article_draft")
            .addOnCompleteListener { }

        FirebaseMessaging.getInstance().subscribeToTopic("new_article_update")
            .addOnCompleteListener { }

    }
}