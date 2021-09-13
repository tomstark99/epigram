package com.epigram.android.ui.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.epigram.android.R
import com.epigram.android.data.arch.utils.LoadNextPage
import com.epigram.android.data.arch.utils.Utils
import com.epigram.android.data.model.Post
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class AdapterSearch (var context: Context, posts: List<Post>, var loadNext: LoadNextPage) : RecyclerView.Adapter<AdapterSearch.ViewHolder>() {

    var posts = mutableListOf<Post>()
    var resultTotal = 0
    var multiTransformation = MultiTransformation<Bitmap>(CenterCrop(), RoundedCorners(40))

    init {
        this.posts = posts.toMutableList()
    }

    enum class Inflater(val id: Int, @LayoutRes val element: Int){
        POSITION_ONE(0, R.layout.element_news_article_first),
        POSITION_MRE(1, R.layout.element_news_article)
    }

    inner class ViewHolder(var l: LinearLayout) : RecyclerView.ViewHolder(l) {
        var title: TextView = l.findViewById(R.id.post_title)
        var image: ImageView = l.findViewById(R.id.post_image)
        var tags: RecyclerView = l.findViewById(R.id.recycler_view_tag)
        var date: TextView = l.findViewById(R.id.post_date_alternate)
        var results: TextView? = l.findViewById(R.id.search_results_number)

        var imageLoaded = false
        var layout: LinearLayout = l
        var disposable: Disposable? = null
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.disposable?.let { disposable -> disposable.dispose() }
        holder.disposable = RxView.clicks(holder.layout)
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { empty ->
                holder.image.transitionName = "article_header"
                loadNext.onPostClicked(
                    posts[holder.adapterPosition],
                    if (holder.imageLoaded) holder.image else null
                )
            }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.disposable?.dispose()
    }

    fun initPosts(newPosts: List<Post>) {
        val res = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int { return posts.size }

            override fun getNewListSize(): Int { return newPosts.size }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return posts[oldItemPosition] == newPosts[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return posts[oldItemPosition].id.equals(newPosts[newItemPosition].id)
            }
        })
        Observable.fromIterable(newPosts)
            .distinct({ it -> it.id })
            .toList()
            .subscribe({it -> posts = it})
        res.dispatchUpdatesTo(this)
    }

    fun addPosts(new: List<Post>) {
        val checkSame = mutableListOf<Post>()
        checkSame.addAll(posts)
        checkSame.addAll(new)
        val res = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int { return posts.size }

            override fun getNewListSize(): Int { return new.size }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return posts[oldItemPosition] == new[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return posts[oldItemPosition].id.equals(new[newItemPosition].id)
            }
        })
        Observable.fromIterable(checkSame)
            .distinct({ it -> it.id })
            .toList()
            .subscribe({ it -> posts = it})
        res.dispatchUpdatesTo(this)
    }

    fun updateResults(total: Int) {
        resultTotal = total
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val l = LayoutInflater.from(parent.context).inflate(Inflater.values()[viewType].element, parent, false) as LinearLayout //else LayoutInflater.from(parent.context).inflate(R.layout.element_news_article, parent, false) as LinearLayout
        return ViewHolder(l)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tags.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        holder.tags.itemAnimator = DefaultItemAnimator()
        holder.tags.adapter = AdapterTag(posts[position].tags)
        setPost(holder,position)
    }

    fun setPost(holder: ViewHolder, position: Int) {
        if(position == 0) holder.results!!.setText(
            holder.results!!.getResources().getQuantityString(
                R.plurals.results,
                resultTotal,
                resultTotal
            ))
        holder.title.text = posts[position].title
        holder.date.text = Utils.dateText(posts[position].date)//posts[position].date.toString("MMM d, yyyy")
        Glide.with(holder.image)
            .load(posts[position].image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.placeholder_background)
            .apply(RequestOptions.bitmapTransform(multiTransformation))
            .listener(
                object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        holder.imageLoaded = false
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        holder.imageLoaded = true
                        return false
                    }
                }
            ).into(holder.image)

        if(position > itemCount - 2) loadNext.bottomReached()
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0) 0 else 1
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun clear() {
        posts.clear()
        notifyDataSetChanged()
    }
}