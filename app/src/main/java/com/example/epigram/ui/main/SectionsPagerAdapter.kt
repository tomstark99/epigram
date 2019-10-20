package com.example.epigram.ui.main

import android.content.Context
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.epigram.R

import java.util.HashMap

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val mContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragmentMap = HashMap<Int, PlaceholderFragment>()

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (!fragmentMap.containsKey(position)) {
            fragmentMap[position] = PlaceholderFragment.newInstance(FILTERS[position], position)
        }
        return fragmentMap[position]!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mContext.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 10
    }

    fun setTabTitles(position: Int, view: MainActivity) {
        if (position == 0) {
            val tab = view.findViewById<TextView>(R.id.tab_text)
            tab.text = "RECENT NEWS"
        } else if (position == 1) {
            val tab = view.findViewById<TextView>(R.id.tab_text)
            tab.text = "NEWS SECTION"
        } else if (position == 2) {
            val tab = view.findViewById<TextView>(R.id.tab_text)
            tab.text = "FEATURES SECTION"
        } else {
            val tab = view.findViewById<TextView>(R.id.tab_text)
            tab.text = "COMMENT SECTION"
        }
    }

    fun fragmentReselected(index: Int) {
        fragmentMap[index]?.reselected()
    }

    companion object {

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2,
            R.string.tab_text_3,
            R.string.tab_text_4,
            R.string.tab_text_5,
            R.string.tab_text_6,
            R.string.tab_text_7,
            R.string.tab_text_8,
            R.string.tab_text_9,
            R.string.tab_text_10
        )
        private val FILTERS = intArrayOf(
            R.string.empty,
            R.string.tag1,
            R.string.tag2,
            R.string.tag3,
            R.string.tag4,
            R.string.tag5,
            R.string.tag6,
            R.string.tag7,
            R.string.tag8,
            R.string.tag9
        )
    }

}