package com.example.epigram;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.epigram.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity{

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

        // set tab header
//        int position = tabs.getSelectedTabPosition();
//
//        System.out.println("Current position: "+ position);
//
//        if (position == 0){
//            TextView tab = findViewById(R.id.tab_text);
//            tab.setText(R.string.tab_1);
//        }
//        else if (position == 1){
//            TextView tab = findViewById(R.id.tab_text);
//            tab.setText(R.string.tab_2);
//        }
//        else if (position == 2){
//            TextView tab = findViewById(R.id.tab_text);
//            tab.setText(R.string.tab_3);
//        }
//        else {
//            TextView tab = findViewById(R.id.tab_text);
//            tab.setText(R.string.tab_4);
        //}

        TextView view = findViewById(R.id.title);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/lora_regular.ttf");
        view.setTypeface(typeFace);
    }



}