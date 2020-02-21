package com.epigram.android.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epigram.android.R
import io.reactivex.disposables.Disposable

import java.util.ArrayList
import java.util.Arrays

class MyAdapterTag(tags: List<String>) :
    RecyclerView.Adapter<MyAdapterTag.MyViewHolder>() {

    var tags: MutableList<String> = ArrayList()


    fun clear() {
        tags.clear()
        notifyDataSetChanged()
    }

    inner class MyViewHolder(var linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout) {

        var tag: TextView
        val disposable: Disposable? = null

        init {
            tag = linearLayout.findViewById(R.id.article_tag_text)
        }
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder.disposable != null) {
            holder.disposable.dispose()
        }
    }

    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        super.onViewAttachedToWindow(holder)

        if (holder.disposable != null) {
            holder.disposable.dispose()
        }
    }

    init {
        this.tags = tags.toMutableList()
        this.tags.removeAll(Arrays.asList("featured top", "carousel", "one sidebar", "weeklytop"))
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val l = LayoutInflater.from(parent.context).inflate(R.layout.element_tag, parent, false) as LinearLayout
        return MyViewHolder(l)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        setTag(holder, position)
    }

    fun setTag(holder: MyViewHolder, position: Int) {
        //tags.removeAll(Arrays.asList("featured top", "carousel", "one sidebar"))
        holder.tag.text = tags[position].toUpperCase()
    }

    override fun getItemCount(): Int {
        return tags.size
    }

}


