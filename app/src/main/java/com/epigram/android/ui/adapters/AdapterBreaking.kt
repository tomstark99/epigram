package com.epigram.android.ui.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.element_news_article.view.*
import java.util.concurrent.TimeUnit

class AdapterBreaking(var context: Context, var posts: MutableList<Post>, var loadNext: LoadNextPage) : RecyclerView.Adapter<AdapterBreaking.ViewHolder>(){

    var multiTransformation = MultiTransformation(CenterCrop(), RoundedCorners(40))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tags.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.tags.itemAnimator = DefaultItemAnimator()
        holder.tags.adapter = AdapterTag(posts[position].tags)
        holder.title.text = posts[position].title
        holder.date.text = Utils.dateText(posts[position].date)//posts[position].date.toString("MMM d, yyyy")
        setImage(holder, position)
    }



    private fun setImage(holder: ViewHolder, position: Int){
        Glide.with(holder.image)
            .load(posts[position].image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.placeholder_background)
            .apply(RequestOptions.bitmapTransform(multiTransformation))
            .listener(
                object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        holder.imgLoaded = false
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        holder.imgLoaded = true
                        return false
                    }
                }
            ).into(holder.image)

    }

    fun addPosts(posts: List<Post>) {
        this.posts.addAll(posts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_news_article_breaking, parent, false))
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.disposable?.dispose()
        holder.disposable = RxView.clicks(holder.view)
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { empty ->
                holder.image.transitionName = "article_header"
                loadNext.onPostClicked(
                    posts[holder.adapterPosition],
                    if (holder.imgLoaded) holder.image else null
                )
            }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.disposable?.dispose()
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val image = view.post_image
        val title = view.post_title
        val date = view.post_date_alternate
        val tags = view.recycler_view_tag
        val view = view

        var disposable: Disposable? = null
        var imgLoaded = false
    }
}