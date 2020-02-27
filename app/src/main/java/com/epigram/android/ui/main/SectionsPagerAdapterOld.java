package com.epigram.android.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.epigram.android.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapterOld extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4, R.string.tab_text_5, R.string.tab_text_6, R.string.tab_text_7};
    private static final int[] FILTERS = new int[]{R.string.empty, R.string.tag1, R.string.tag2, R.string.tag3, R.string.tag4, R.string.tag5, R.string.tag6};

    private Map<Integer, PlaceholderFragment> fragmentMap= new HashMap<>();

    private final Context mContext;

    public SectionsPagerAdapterOld(Context context, FragmentManager fm) {
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
        return 7;
    }

    public void fragmentReselected(int index){
        fragmentMap.get(index).reselected();
    }

}