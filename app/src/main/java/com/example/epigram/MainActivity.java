package com.example.epigram;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import com.example.epigram.ui.main.SectionsPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    static String ARG_SECTION = "section.object";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                sectionsPagerAdapter.fragmentReselected(tab.getPosition());
            }
        });

        TextView title = findViewById(R.id.title);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/lora_bold.ttf");
        title.setTypeface(typeFace);
        NavigationView navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        TextView titleNav = headerView.findViewById(R.id.nav_title);
        titleNav.setTypeface(typeFace);

        navView.setNavigationItemSelectedListener(this);

        ImageView search = findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.start(MainActivity.this);
            }
        });

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ImageView nav = findViewById(R.id.open_nav);
        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("new_article")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

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



    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        switch (menuItem.getItemId()){
            //case R.id.nav_home :
            //    break;
            case R.id.nav_news :
                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_news_tag));
                break;
            case R.id.nav_features :
                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_features_tag));
                break;
            case R.id.nav_comment :
                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_comment_tag));
                break;
            case R.id.nav_the_croft:
                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_the_croft_tag));
                break;
//            case R.id.nav_opinion:
//                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_opinion_tag));
//                break;
            case R.id.nav_science:
                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_science_tag));
                break;
            case R.id.nav_wellbeing:
                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_wellbeing_tag));
                break;
            case R.id.nav_lifestyle:
                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_lifestyle_tag));
                break;
            case R.id.nav_entertainment:
                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_entertainment_tag));
                break;
            case R.id.nav_sport:
                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_sport_tag));
                break;
            case R.id.nav_intramural:
                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_intramural_tag));
                break;
            case R.id.nav_puzzles:
                SectionActivity.Companion.start(this, menuItem.toString(), getString(R.string.menu_puzzles_tag));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
                default:

                    drawerLayout.closeDrawer(GravityCompat.START);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}