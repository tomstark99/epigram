package com.epigram.android.ui.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.epigram.android.R
import com.epigram.android.data.arch.utils.LoadNextPage
import com.epigram.android.data.arch.utils.SnapHelperOne
import com.epigram.android.data.model.Post
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class AdapterArticlesHome(context: Context, posts: MutableList<Post>, var breakingPosts: MutableList<Post>, loadNext: LoadNextPage, position: Int) : RecyclerView.Adapter<AdapterArticlesHome.MyViewHolder>(){

    var posts: MutableList<Post> = ArrayList()
    var context: Context
    var loadNextPage: LoadNextPage
    var multiTransformation = MultiTransformation(CenterCrop(), RoundedCorners(40))
    var pageIndex: Int = 0

    init {
        this.posts = posts
        this.context = context
        this.loadNextPage = loadNext
        this.pageIndex = position
    }

    enum class Inflater(val id: Int, @LayoutRes val element: Int){
        POSITION_ONE(0, R.layout.element_news_article_breaking_list),
        POSITION_THR(1, R.layout.element_news_article_first),
        POSITION_MRE(2, R.layout.element_news_article),
        POSITION_HME(3, R.layout.element_corona)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position == 1) {
            val snapHelper = SnapHelperOne()
            holder.breaking!!.onFlingListener = null
            snapHelper.attachToRecyclerView(holder.breaking!!)
            holder.breaking!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.breaking!!.adapter = BreakingAdapter(context, breakingPosts, loadNextPage)
        }
        else if(position > 1) {
            holder.tags!!.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            holder.tags!!.itemAnimator = DefaultItemAnimator()
            holder.tags!!.adapter = MyAdapterTag(posts[position-2].tags.orEmpty())
            setPost(holder, position-2)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val l = LayoutInflater.from(parent.context).inflate(Inflater.values()[viewType].element, parent, false) as LinearLayout
        return MyViewHolder(l)

    }

    override fun getItemViewType(position: Int): Int {
        if(position == 0) return 3
        else if(position == 1) return 0
        return if(position == 2) 1 else 2
    }

    override fun getItemCount(): Int {
        return posts.size + 1
    }

    inner class MyViewHolder(l: LinearLayout) : RecyclerView.ViewHolder(l) {

        var title: TextView?
        var articleImage: ImageView?
        var tags: RecyclerView?
        var breaking: RecyclerView?
        var date: TextView?

        var firstElementText: TextView?
        var imageLoaded = false
        var linearLayout: LinearLayout
        var disposable: Disposable? = null

        init {
            title = l.findViewById(R.id.post_title)
            articleImage = l.findViewById(R.id.post_image)
            date = l.findViewById(R.id.post_date_alternate)
            tags = l.findViewById(R.id.recycler_view_tag)
            breaking = l.findViewById(R.id.recycler_breaking)
            firstElementText = l.findViewById(R.id.search_results_number)

            linearLayout = l
        }
    }

    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.disposable?.let { disposable -> disposable.dispose() }
        holder.disposable = RxView.clicks(holder.linearLayout)
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { empty ->
                if (holder.adapterPosition > 1) {
                    holder.articleImage!!.setTransitionName("article_header")
                    loadNextPage.onPostClicked(
                        posts[holder.adapterPosition-2],
                        if (holder.imageLoaded) holder.articleImage else null
                    )
                }
            }
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.disposable?.let { disposable -> disposable.dispose() }
    }

    fun addPosts(newPosts: List<Post>){
        val posts2 = posts
        posts2.addAll(newPosts)
        Observable.fromIterable(posts2)
            .distinct({it.id})
            .toList()
            .subscribe({it -> posts = it})
        notifyDataSetChanged()
    }

    fun setPost(holder: MyViewHolder, position: Int){
//        if(position == 0 && posts[position].date.plusWeeks(1).isBeforeNow && posts[position].tags!!.contains("breaking news") && (!posts[position].title.contains("coronavirus") || !posts[position].title.contains("corona"))) {
//            holder.itemView.visibility = View.GONE
//            holder.itemView.layoutParams =  RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1)
//        } else {
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams =  RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
//        }
        holder.title!!.text = posts[position].title
        holder.date!!.text = posts[position].date.toString("MMM d, yyyy")
        Glide.with(holder.articleImage!!)
            .load(posts[position].image)
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
            ).into(holder.articleImage!!)

        if(position > itemCount - 4) {
            loadNextPage.bottomReached()
        }

    }

    fun clear(){
        posts.clear()
        notifyDataSetChanged()
    }

}