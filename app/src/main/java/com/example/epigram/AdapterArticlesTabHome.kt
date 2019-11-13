package com.example.epigram

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epigram.data.Post
import kotlinx.android.synthetic.main.element_section.view.*

class AdapterArticlesTabHome(var context: Context, var posts: MutableList<Post>, var postsBreaking: MutableList<Post>, var loadNext: AdapterArticles.LoadNextPage, var position: Int) : RecyclerView.Adapter<AdapterArticlesTabHome.ViewHolder>(){

    enum class Positions(@StringRes val title: Int){
        ONE(R.string.section_breaking),
        TWO(R.string.section_article)
    }

    private var map = mutableMapOf<Positions, RecyclerView.Adapter<*>>(Positions.ONE to AdapterArticlesBreaking(context, postsBreaking, loadNext, position),
                                                                        Positions.TWO to AdapterArticles(context, posts, loadNext, position))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val positions = Positions.values()[position]
        holder.recyclerView.layoutManager = LinearLayoutManager(this.context)
        holder.recyclerView.itemAnimator = DefaultItemAnimator()
        holder.recyclerView.adapter = map[positions]!!
        holder.title.setText(positions.title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_section, parent, false))
    }

    override fun getItemCount(): Int {
        return map.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val title = view.sub_heading_text
        val recyclerView = view.recycler_article
    }
}