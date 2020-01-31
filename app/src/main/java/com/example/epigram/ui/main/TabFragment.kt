package com.example.epigram.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.epigram.AdapterArticles
import com.example.epigram.ArticleActivity
import com.example.epigram.data.Post
import java.text.FieldPosition

class TabFragment : Fragment() , AdapterArticles.LoadNextPage{


    var pageIndex = 0

    enum class Page(val id: Int)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun bottomReached() {
    }

    override fun onPostClicked(clicked: Post, imageView: ImageView?) {
        if (imageView != null) {
            ArticleActivity.start(activity!!, clicked, imageView)
        } else {
            ArticleActivity.start(activity!!, clicked)
        }
    }

    companion object{

        val ARG_SECTION_NUMBER = "section_number"

        fun newInstance(index: Int, position: Int): PlaceholderFragment{
            var fragment = PlaceholderFragment()
            var bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle
            //fragment.pageIndex = position
            return fragment
        }
    }

}