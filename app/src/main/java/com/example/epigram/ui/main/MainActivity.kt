package com.example.epigram.ui.main

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.epigram.R
import com.example.epigram.ui.search.SearchActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs = findViewById<TabLayout>(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                sectionsPagerAdapter.fragmentReselected(tab.position)
            }
        })

        val view = findViewById<TextView>(R.id.title)
        val typeFace = Typeface.createFromAsset(assets, "fonts/lora_regular.ttf")
        view.typeface = typeFace

        val search = findViewById<ImageView>(R.id.search_button)
        search.setOnClickListener { SearchActivity.start(this@MainActivity) }

        FirebaseMessaging.getInstance().subscribeToTopic("new_article")
            .addOnCompleteListener { }

        FirebaseMessaging.getInstance().subscribeToTopic("new_article_draft")
            .addOnCompleteListener { }

        FirebaseMessaging.getInstance().subscribeToTopic("new_article_update")
            .addOnCompleteListener { }

    }

    override fun onSearchRequested(): Boolean {
        return super.onSearchRequested()

    }


}