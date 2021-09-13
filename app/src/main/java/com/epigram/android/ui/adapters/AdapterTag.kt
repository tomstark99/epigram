package com.epigram.android.ui.adapters

import android.app.Activity
import android.content.Context
import android.os.Debug
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epigram.android.R
import com.epigram.android.ui.section.SectionActivity
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber

import java.util.ArrayList
import java.util.Arrays
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class AdapterTag(tags: Pair<List<String>?, List<String>?>) :
    RecyclerView.Adapter<AdapterTag.MyViewHolder>() {

    var tags: MutableList<String> = ArrayList()
    var slugs: MutableList<String> = ArrayList()
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
            .subscribe ({ empty ->
                println(empty)
                if (context !is SectionActivity || (context as SectionActivity).tag1 != slugs[holder.adapterPosition]) {
                    SectionActivity.start(
                        context as Activity,
                        tags[holder.adapterPosition],
                        slugs[holder.adapterPosition]
                    )
                }
            }, { e -> Timber.e(e, "Tag: ${slugs[holder.adapterPosition]} is already loaded") })
    }

    init {
        this.tags = tags.first.orEmpty().toMutableList()
        this.slugs = tags.second.orEmpty().toMutableList()
        this.tags.removeAll(Arrays.asList("featured top", "carousel", "one sidebar", "weeklytop", "no sidebar"))
        this.slugs.removeAll(Arrays.asList("featured-top", "carousel", "one-sidebar", "weeklytop", "no-sidebar"))
        val tagsCorrected = this.tags.map { if (it == "film&tv") "film & tv" else it }.toMutableList()
        this.tags = tagsCorrected

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val l = LayoutInflater.from(parent.context).inflate(R.layout.element_tag, parent, false) as LinearLayout
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


