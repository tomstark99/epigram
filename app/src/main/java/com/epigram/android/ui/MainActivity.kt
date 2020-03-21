package com.epigram.android.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.epigram.android.R

import com.epigram.android.data.arch.android.BaseActivity
import com.epigram.android.ui.about.AboutActivity
import com.epigram.android.ui.main.SectionsPagerAdapter
import com.epigram.android.ui.promo.PromoActivity
import com.epigram.android.ui.search.SearchActivity
import com.epigram.android.ui.section.SectionActivity
import com.epigram.android.ui.settings.SettingsActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.app_bar_main.*
import org.joda.time.DateTime

class MainActivity : BaseActivity<MainActivityMvp.Presenter>(),
    NavigationView.OnNavigationItemSelectedListener, MainActivityMvp.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // if(DateTime("2020-03-16T00:00:00.000").isAfterNow) setContentView(R.layout.activity_main_p) else
        presenter = MainActivityPresenter(this)
        presenter.onCreate()
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 0
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

        val title = findViewById<TextView>(R.id.title)
        val typeFace = Typeface.createFromAsset(assets, "fonts/lora_bold.ttf")
        title.typeface = typeFace
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val titleNav = headerView.findViewById<TextView>(R.id.nav_title)
        titleNav.typeface = typeFace

        navView.setNavigationItemSelectedListener(this)

        val search = findViewById<ImageView>(R.id.search_button)
        search.setOnClickListener { SearchActivity.start(this@MainActivity) }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val nav = findViewById<ImageView>(R.id.open_nav)
        nav.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        // notifications enabled
        FirebaseMessaging.getInstance().subscribeToTopic("new_article")
            .addOnCompleteListener { }

//        FirebaseMessaging.getInstance().subscribeToTopic("new_article_draft")
//            .addOnCompleteListener { }

        //        FirebaseMessaging.getInstance().subscribeToTopic("new_article_draft")
        //                .addOnCompleteListener(new OnCompleteListener<Void>() {
        //                    @Override
        //                    public void onComplete(@NonNull Task<Void> task) {
        //
        //                    }
        //                });
        //
        //        FirebaseMessaging.getInstance().subscribeToTopic("new_article_update")
        //                .addOnCompleteListener(new OnCompleteListener<Void>() {
        //                    @Override
        //                    public void onComplete(@NonNull Task<Void> task) {
        //
        //                    }
        //                });

    }

    override fun load(showWelcome: Boolean) {
        //tabs.getTabAt(0)!!.setIcon(R.drawable.ic_warning_amber_24px_outlined_red)
        if (showWelcome && DateTime("2020-03-16T00:00:00.000").isAfterNow) {
            WelcomeActivity.start(this)
        }
    }

    override fun onSearchRequested(): Boolean {
        return super.onSearchRequested()

    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        when (menuItem.itemId) {
            //case R.id.nav_home :
            //    break;
            R.id.nav_most_read -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_most_read_tag)
            )
            R.id.nav_news -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_news_tag)
            )
            R.id.nav_features -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_features_tag)
            )
            R.id.nav_comment -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_comment_tag)
            )
            R.id.nav_the_croft -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_the_croft_tag)
            )
            R.id.nav_wellbeing -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_wellbeing_tag)
            )
            R.id.nav_food -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_food_tag)
            )
            R.id.nav_travel -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_travel_tag)
            )
            R.id.nav_style -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_style_tag)
            )
            //            case R.id.nav_opinion:
            //                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_opinion_tag));
            //                break;
            R.id.nav_science -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_science_tag)
            )
            R.id.nav_entertainment -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_entertainment_tag)
            )
            R.id.nav_film_tv -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_film_tv_tag)
            )
            R.id.nav_arts -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_arts_tag)
            )
            R.id.nav_music -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_music_tag)
            )
            R.id.nav_sport -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_sport_tag)
            )
            R.id.nav_intramural -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_intramural_tag)
            )
            R.id.nav_puzzles -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_puzzles_tag)
            )
            R.id.nav_promo -> startActivity(Intent(this, PromoActivity::class.java))
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_about -> startActivity(Intent(this, AboutActivity::class.java))
            else ->

                drawerLayout.closeDrawer(GravityCompat.START)
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {

        internal var ARG_SECTION = "section.object"
    }
}