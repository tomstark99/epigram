package com.example.epigram.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.example.epigram.R

import com.example.epigram.arch.android.BaseActivity
import com.example.epigram.ui.about.AboutActivity
import com.example.epigram.ui.main.SectionsPagerAdapter
import com.example.epigram.ui.search.SearchActivity
import com.example.epigram.ui.search.SearchActivity1
import com.example.epigram.ui.section.SectionActivity
import com.example.epigram.ui.settings.SettingsActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import android.widget.ExpandableListView
import com.example.epigram.data.ExpandedMenuModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.example.epigram.ui.main.ExpandableListAdapter
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : BaseActivity<MainActivityMvp.Presenter>(),
    NavigationView.OnNavigationItemSelectedListener, MainActivityMvp.View {

    private var drawerLayout: DrawerLayout? = null
    var menuAdapter: ExpandableListAdapter? = null
    var expandableList: ExpandableListView? = null
    var listHeader = mutableListOf<ExpandedMenuModel>()
    var listChild = HashMap<ExpandedMenuModel, List<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainActivityPresenter(this)
        presenter.onCreate()
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)
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
//        val navView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = nav_view.getHeaderView(0)
        val titleNav = headerView.findViewById<TextView>(R.id.nav_title)
        titleNav.typeface = typeFace
//        navView.setNavigationItemSelectedListener(this)

        search_button.setOnClickListener { SearchActivity.start(this@MainActivity) }

//        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
//        val nav = findViewById<ImageView>(R.id.open_nav)
        open_nav.setOnClickListener {
            if (!drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }

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


        if(nav_view != null) setupDrawerContent(nav_view)
        prepareListData()
        menuAdapter = ExpandableListAdapter(this, listHeader, listChild, nav_list_view)
        nav_list_view.setAdapter(menuAdapter)
        nav_list_view.setOnChildClickListener { expandableListView, view, i, i2, l ->
            false
        }
        nav_list_view.setOnGroupClickListener { expandableListView, view, i, l ->
            false
        }
        nav_list_view.setGroupIndicator(null)
        nav_list_view.setChildIndicator(null)
    }

    fun setupDrawerContent(nav: NavigationView) {
        nav.setNavigationItemSelectedListener {
            it.setChecked(true)
            drawer_layout.closeDrawers()
            true
        }
    }

    fun prepareListData() {
        var item1 = ExpandedMenuModel()
        item1.setIconName("heading1")
        item1.setIconImg(R.drawable.ic_arrow_drop_down_black_24dp)
        var item2 = ExpandedMenuModel()
        item2.setIconName("heading2")
        item2.setIconImg(R.drawable.ic_arrow_drop_down_black_24dp)
        var item3 = ExpandedMenuModel()
        item3.setIconName("heading3")
        item3.setIconImg(R.drawable.ic_arrow_drop_down_black_24dp)
        listHeader.add(item1)
        listHeader.add(item2)
        listHeader.add(item3)

        var heading1 = mutableListOf<String>()
        heading1.add("Submenu of item 1")

        var heading2 = mutableListOf<String>()
        heading2.add("submenu of item 2")
        heading2.add("submenu of item 2")
        heading2.add("submenu of item 2")

        listChild.put(listHeader[0], heading1)
        listChild.put(listHeader[1], heading2)
    }

    override fun load(showWelcome: Boolean) {
        if (showWelcome) {
            WelcomeActivity.start(this)
        }
    }

    override fun onSearchRequested(): Boolean {
        return super.onSearchRequested()

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            //case R.id.nav_home :
            //    break;
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
            //            case R.id.nav_opinion:
            //                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_opinion_tag));
            //                break;
            R.id.nav_science -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_science_tag)
            )
            R.id.nav_wellbeing -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_wellbeing_tag)
            )
            R.id.nav_lifestyle -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_lifestyle_tag)
            )
            R.id.nav_entertainment -> SectionActivity.start(
                this,
                menuItem.toString(),
                getString(R.string.menu_entertainment_tag)
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
            //R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_about -> startActivity(Intent(this, AboutActivity::class.java))
            else ->

                drawer_layout.closeDrawer(GravityCompat.START)
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        internal var ARG_SECTION = "section.object"
    }
}