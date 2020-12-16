package com.epigram.android.ui.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epigram.android.R
import com.epigram.android.ui.author.AuthorActivity
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

import java.util.ArrayList
import java.util.concurrent.TimeUnit

class AdapterAuthorTag(tags: Triple<List<String>?, List<String>?, List<String>?>) :
    RecyclerView.Adapter<AdapterAuthorTag.MyViewHolder>() {

    var tags: MutableList<String> = ArrayList()
    var slugs: MutableList<String> = ArrayList()
    var image: MutableList<String> = ArrayList()
    lateinit var context: Context

    fun clear() {
        tags.clear()
        notifyDataSetChanged()
    }

    inner class MyViewHolder(var linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout) {

        var tag: TextView
        var disposable: Disposable? = null

        init {
            tag = linearLayout.findViewById(R.id.article_tag_text)
        }
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.disposable?.let { disposable -> disposable.dispose() }
    }

//    override fun onViewAttachedToWindow(holder: MyViewHolder) {
//        super.onViewAttachedToWindow(holder)
//
//        if (holder.disposable != null) {
//            holder.disposable.dispose()
//        }
//    }

    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.disposable?.let { disposable -> disposable.dispose() }
        holder.disposable = RxView.clicks(holder.linearLayout)
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { empty ->
                AuthorActivity.start(context as Activity, tags[holder.adapterPosition], slugs[holder.adapterPosition], image[holder.adapterPosition])
            }
    }

    init {
        this.tags = tags.first.orEmpty().toMutableList()
        this.slugs = tags.second.orEmpty().toMutableList()
        this.image = tags.third.orEmpty().toMutableList()

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val l = LayoutInflater.from(parent.context).inflate(R.layout.element_tag_author, parent, false) as LinearLayout
        return MyViewHolder(l)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        setTag(holder, position)
    }

    fun setTag(holder: MyViewHolder, position: Int) {
        holder.tag.text = tags[position].toUpperCase()
    }

    override fun getItemCount(): Int {
        return tags.size
    }

}


