package com.example.epigram

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.example.epigram.data.models.Post
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.element_news_article.view.*

import java.util.concurrent.TimeUnit

class PostAdapter(
    private val onBottomReached: (() -> Unit)?,
    private val onPostClicked: ((post: Post, imageView: ImageView?) -> Unit)?
) : RecyclerView.Adapter<PostAdapter.AdapterViewHolder>() {

    private val multiTransformation = MultiTransformation(CenterCrop(), RoundedCorners(32))

    var posts = emptyList<Post>()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return posts.size
                }

                override fun getNewListSize(): Int {
                    return value.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return posts[oldItemPosition] == value[newItemPosition]
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return posts[oldItemPosition].uuid == value[newItemPosition].uuid
                }
            })
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    var breaking: Post? = null
        set(value) {
            field = value
            notifyItemInserted(0)
        }

    var totalResults: Int? = null
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    fun clear() {
        posts = emptyList()
        notifyDataSetChanged()
    }

    override fun onViewDetachedFromWindow(holder: AdapterViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.disposable?.dispose()
    }

    override fun onViewAttachedToWindow(holder: AdapterViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.disposable?.dispose()
        holder.disposable = RxView.clicks(holder.itemView)
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                holder.itemView.post_image.transitionName = "article_header"
                val position = getCorrectPosition(holder.adapterPosition)
                val post = if (position >= 0) posts[position] else breaking!!
                onPostClicked?.invoke(
                    post,
                    if (holder.imageLoaded) holder.itemView.post_image else null
                )
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.element_news_article, parent, false)
        return AdapterViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        if (position == 0 && breaking != null) {
            breakingPost(holder, breaking!!)
        } else {
            normalPost(holder, position)
        }
    }

    private fun breakingPost(holder: AdapterViewHolder, post: Post) {
        holder.itemView.post_recent.visibility = View.VISIBLE
        holder.itemView.post_breaking.visibility = View.VISIBLE
        holder.itemView.post_breaking.setText(R.string.breaking_news)

        holder.itemView.post_title.text = post.title
        holder.itemView.post_tag.text = post.tag
        holder.itemView.post_date.text = post.date.toString("MMM d, yyyy")
        post.image?.let {
            loadImage(holder, it)
        }
    }

    private fun normalPost(holder: AdapterViewHolder, position: Int) {
        val post = posts[getCorrectPosition(position)]
        if (totalResults != null && position == 0) {
            holder.itemView.post_breaking.visibility = View.VISIBLE
            holder.itemView.post_breaking.text = holder.itemView.post_date.resources
                .getQuantityString(R.plurals.results, totalResults!!, totalResults!!)
        } else {
            holder.itemView.post_breaking.visibility = View.GONE
        }
        holder.itemView.post_recent.visibility = View.GONE

        holder.itemView.post_title.text = post.title
        holder.itemView.post_tag.text = post.tag
        holder.itemView.post_date.text = post.date.toString("MMM d, yyyy")
        post.image?.let {
            loadImage(holder, it)
        }

        if (position > itemCount - 2) onBottomReached?.invoke()
    }

    private fun loadImage(holder: AdapterViewHolder, url: String) {
        Glide.with(holder.itemView.post_image).load(url).placeholder(R.drawable.placeholder_background)
            .apply(RequestOptions.bitmapTransform(multiTransformation)).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.imageLoaded = false
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.imageLoaded = true
                    return false
                }
            }).into(holder.itemView.post_image)
    }

    private fun getCorrectPosition(position: Int): Int {
        return if (breaking != null) {
            position - 1
        } else {
            position
        }
    }

    override fun getItemCount(): Int {
        return posts.size + (if (breaking != null) 1 else 0)
    }

    inner class AdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageLoaded = false
        var disposable: Disposable? = null
    }

}


