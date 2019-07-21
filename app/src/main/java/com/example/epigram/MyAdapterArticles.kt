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
import java.util.concurrent.TimeUnit

class MyAdapterArticles(
    posts: MutableList<Post>, loadNext: LoadNextPage, private val pageIndex: Int // 100 for search recycler view
) : RecyclerView.Adapter<MyAdapterArticles.MyViewHolder>() {

    var posts: MutableList<Post> = ArrayList<Post>()
    private val multiTransformation: MultiTransformation<Bitmap>
    private var loadNextPage: LoadNextPage? = null

    private var resultTotal = 0

    fun clear() {
        posts.clear()
        notifyDataSetChanged()
    }

    fun setPostList(checkSame: MutableList<Post>) {

        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return posts.size
            }

            override fun getNewListSize(): Int {
                return checkSame.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return posts[oldItemPosition] === checkSame[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return posts[oldItemPosition].getId().equals(checkSame[newItemPosition].getId())
            }
        })
        posts = checkSame
        diffResult.dispatchUpdatesTo(this)
    }

    inner class MyViewHolder(var linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout) {

        var title: TextView
        var titleImage: ImageView
        var tag: TextView
        // public TextView date; // for date on top of image
        var dateAlt: TextView
        //public TextView sectionTitle;
        var searchResults: TextView

        var imageLoaded = false

        private val disposable: Disposable? = null

        init {
            if (pageIndex == SEARCH_PAGE_INDEX) {
                searchResults = linearLayout.findViewById(R.id.search_results_number)
            }
            title = linearLayout.findViewById(R.id.post_title)
            tag = linearLayout.findViewById(R.id.tag_text)
            // date = l.findViewById(R.id.post_date); // for top of image
            dateAlt = linearLayout.findViewById(R.id.post_date_alternate)
            titleImage = linearLayout.findViewById(R.id.post_image)
            //sectionTitle = l.findViewById(R.id.section_text);
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
        holder.disposable = RxView.clicks(holder.linearLayout).throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe { empty ->
            holder.titleImage.transitionName = "article_header"

            loadNextPage!!.onPostClicked(
                posts[holder.adapterPosition],
                if (holder.imageLoaded) holder.titleImage else null
            )
        }
    }

    init {
        this.posts = posts
        multiTransformation = MultiTransformation(CenterCrop(), RoundedCorners(32))
        loadNextPage = loadNext
    }

    fun addPosts(postsNew: List<Post>) {

        val checkSame = ArrayList(posts)
        checkSame.addAll(postsNew)

        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return posts.size
            }

            override fun getNewListSize(): Int {
                return checkSame.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return posts[oldItemPosition] === checkSame.get(newItemPosition)
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return posts[oldItemPosition].getId().equals(checkSame.get(newItemPosition).getId())
            }
        })
        posts = checkSame

        diffResult.dispatchUpdatesTo(this)
        //posts = new ListUtils().duplicatePost(new ArrayList<>(posts));
        //notifyDataSetChanged();
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapterArticles.MyViewHolder {
        if (viewType == 1 && pageIndex == HOME_PAGE_INDEX) {
            val l = LayoutInflater.from(parent.context).inflate(
                R.layout.element_news_article_breaking,
                parent,
                false
            ) as LinearLayout
            return MyViewHolder(l)
        } else if (viewType == 1 && pageIndex == SEARCH_PAGE_INDEX) {
            val l = LayoutInflater.from(parent.context).inflate(
                R.layout.element_news_article_first,
                parent,
                false
            ) as LinearLayout
            return MyViewHolder(l)
        } else {
            val l = LayoutInflater.from(parent.context).inflate(
                R.layout.element_news_article,
                parent,
                false
            ) as LinearLayout
            return MyViewHolder(l)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && (pageIndex == HOME_PAGE_INDEX || pageIndex == SEARCH_PAGE_INDEX))
            1
        else
            2
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        setPosts(holder, position)
    }

    fun setPosts(holder: MyViewHolder, position: Int) {
        if (pageIndex == SEARCH_PAGE_INDEX && position == 0) {
            holder.searchResults.text =
                holder.searchResults.resources.getQuantityString(
                    R.plurals.results,
                    resultTotal,
                    resultTotal
                )//Integer.toString(posts.size()));
        }
        holder.title.setText(posts[position].getTitle())
        holder.tag.setText(posts[position].getTag())
        //holder.date.setText(posts.get(position).getDate().toString("MMM d, yyyy")); // date for on top of image
        holder.dateAlt.setText(posts[position].getDate().toString("MMM d, yyyy"))

        Glide.with(holder.titleImage).load(posts[position].getImage()).placeholder(R.drawable.placeholder_background)
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
        }).into(holder.titleImage)
        if (position > itemCount - 2) loadNextPage!!.bottomReached()
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    interface LoadNextPage {
        fun bottomReached()

        fun onPostClicked(clicked: Post, titleImage: ImageView?)
    }

    fun setResultTotal(total: Int) {
        resultTotal = total
        notifyItemChanged(0)
    }

    companion object {

        var SEARCH_PAGE_INDEX = 100
        var HOME_PAGE_INDEX = 0
    }

}


