package com.example.epigram.ui.main;

import android.content.Context;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.epigram.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4, R.string.tab_text_5, R.string.tab_text_6, R.string.tab_text_7, R.string.tab_text_8, R.string.tab_text_9, R.string.tab_text_10};
    private static final int[] FILTERS = new int[]{R.string.empty, R.string.tag1, R.string.tag2, R.string.tag3, R.string.tag4, R.string.tag5, R.string.tag6, R.string.tag7, R.string.tag8, R.string.tag9};

    private Map<Integer, PlaceholderFragment> fragmentMap= new HashMap<>();

    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(!fragmentMap.containsKey(position)){
            fragmentMap.put(position, PlaceholderFragment.newInstance(FILTERS[position], position));
        }
        return fragmentMap.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 10;
    }

    public void setTabTitles(int position, MainActivity view){
        if (position == 0){
            TextView tab = view.findViewById(R.id.tab_text);
            tab.setText("RECENT NEWS");
        }
        else if (position == 1){
            TextView tab = view.findViewById(R.id.tab_text);
            tab.setText("NEWS SECTION");
        }
        else if (position == 2){
            TextView tab = view.findViewById(R.id.tab_text);
            tab.setText("FEATURES SECTION");
        }
        else{
            TextView tab = view.findViewById(R.id.tab_text);
            tab.setText("COMMENT SECTION");
        }
    }

    public void fragmentReselected(int index){
        fragmentMap.get(index).reselected();
    }

}