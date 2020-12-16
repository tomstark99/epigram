package com.epigram.android.ui.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.epigram.android.R
import com.epigram.android.data.arch.PreferenceModule
import com.epigram.android.data.arch.utils.LoadNextPage
import com.epigram.android.data.arch.utils.Utils
import com.epigram.android.data.model.Post
import com.f2prateek.rx.preferences2.Preference
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class AdapterAuthor(context: Context, posts: MutableList<Post>, loadNext: LoadNextPage, position: Int, image: String) : RecyclerView.Adapter<AdapterAuthor.MyViewHolder>(){

    var posts: MutableList<Post> = ArrayList()
    var context: Context
    var loadNextPage: LoadNextPage
    var multiTransformation = MultiTransformation(CenterCrop(), RoundedCorners(40))
    var authorTransformation = MultiTransformation(CircleCrop())
    var pageIndex: Int = 0
    var image: String
    private val l: Preference<Int> = PreferenceModule.layoutMode



    init {
        this.posts = posts
        this.context = context
        this.loadNextPage = loadNext
        this.pageIndex = position
        this.image = image
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (image.isNotEmpty() && position == 0) {
            Glide.with(holder.author_image!!)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.placeholder_author_image)
                .apply(RequestOptions.bitmapTransform(authorTransformation))
                .into(holder.author_image!!)
        } else {
            holder.tags!!.layoutManager =
                LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            holder.tags!!.itemAnimator = DefaultItemAnimator()
            holder.tags!!.adapter = AdapterTag(posts[position - if(image.isEmpty()) 0 else 1].tags)
            setPost(holder, position - if(image.isEmpty()) 0 else 1)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        if(image.isNotEmpty() && viewType == 0) return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_author, parent, false) as LinearLayout)
        else{
            val l = if(l.get() == 1) LayoutInflater.from(parent.context).inflate(R.layout.element_news_article_new, parent, false) as LinearLayout else LayoutInflater.from(parent.context).inflate(R.layout.element_news_article, parent, false) as LinearLayout
            return MyViewHolder(l)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0) 0 else 1
    }


    override fun getItemCount(): Int {
        return posts.size + if(image.isEmpty()) 0 else 1
    }



    inner class MyViewHolder(l: LinearLayout) : RecyclerView.ViewHolder(l) {

        var author_image: ImageView?

        var title: TextView?
        var articleImage: ImageView?
        var tags: RecyclerView?
        var date: TextView?

        var firstElementText: TextView?
        var imageLoaded = false
        var linearLayout: LinearLayout
        var disposable: Disposable? = null
        var frame: LinearLayout?

        init {
            author_image = l.findViewById(R.id.author_image)

            title = l.findViewById(R.id.post_title)
            articleImage = l.findViewById(R.id.post_image)
            date = l.findViewById(R.id.post_date_alternate)
            tags = l.findViewById(R.id.recycler_view_tag)

            firstElementText = l.findViewById(R.id.search_results_number)

            linearLayout = l
            frame = l.findViewById(R.id.frame)
        }
    }



    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.disposable?.let { disposable -> disposable.dispose() }
        if(holder.adapterPosition != 0 && image.isNotEmpty()) {
            holder.disposable = RxView.clicks(holder.linearLayout)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { empty ->
                    holder.articleImage!!.setTransitionName("article_header")
                    loadNextPage.onPostClicked(
                        posts[holder.adapterPosition-1],
                        if (holder.imageLoaded) holder.articleImage else null
                    )
                }
        } else if (image.isEmpty()) {
            holder.disposable = RxView.clicks(holder.linearLayout)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { empty ->
                    holder.articleImage!!.setTransitionName("article_header")
                    loadNextPage.onPostClicked(
                        posts[holder.adapterPosition],
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
        val scale = context.resources.displayMetrics.density
        val dpAsPixels = (6 * scale + 0.5f).toInt()
        if (position == 0) {
            holder.frame!!.setPadding(0, dpAsPixels, 0, 0)
        }
        if(position != 0 && holder.frame!!.paddingTop == dpAsPixels) {
            holder.frame!!.setPadding(0, 0, 0, 0)
        }
        holder.title!!.text = posts[position].title
        holder.date!!.text = Utils.dateText(posts[position].date)//posts[position].date.toString("MMM d, yyyy")
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

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        holder.imageLoaded = true
                        return false
                    }
                }
            ).into(holder.articleImage!!)

        if(position > itemCount - if (image.isEmpty()) 2 else 3) loadNextPage.bottomReached()

    }

    fun clear(){
        posts.clear()
        notifyDataSetChanged()
    }


}