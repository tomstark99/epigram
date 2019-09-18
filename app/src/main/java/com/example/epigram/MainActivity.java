package com.example.epigram;

import android.content.Intent;
import android.drm.DrmStore;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
            case R.id.nav_home :
                break;
            case R.id.nav_news :
                startActivity(new Intent(MainActivity.this, SectionActivity.class));
                break;
            case R.id.nav_features :
                break;
            case R.id.nav_comment :
                break;
            case R.id.nav_the_croft:
                break;
            case R.id.nav_opinion:
                break;
            case R.id.nav_science:
                break;
            case R.id.nav_wellbeing:
                break;
            case R.id.nav_lifestyle:
                break;
            case R.id.nav_entertainment:
                break;
            case R.id.nav_sport:
                break;
            case R.id.nav_intramural:
                break;
            case R.id.nav_puzzles:
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_about:
                break;
                default:

                    drawerLayout.closeDrawer(GravityCompat.START);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}