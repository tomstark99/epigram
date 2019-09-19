package com.example.epigram

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.epigram.data.Post
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

import java.util.ArrayList
import java.util.Arrays
import java.util.concurrent.TimeUnit

class MyAdapterTag(tags: MutableList<String>) :
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
        this.tags = tags
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapterTag.MyViewHolder {
        val l = LayoutInflater.from(parent.context).inflate(R.layout.element_tag, parent, false) as LinearLayout
        return MyViewHolder(l)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0)
            1
        else
            2
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        setTag(holder, position)
    }

    fun setTag(holder: MyViewHolder, position: Int) {
        tags.removeAll(Arrays.asList("featured top", "carousel", "one sidebar"))
        holder.tag.text = tags[position].toUpperCase()
    }

    override fun getItemCount(): Int {
        return tags.size
    }

}


