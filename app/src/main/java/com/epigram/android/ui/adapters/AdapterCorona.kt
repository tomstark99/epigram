package com.epigram.android.ui.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
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

class AdapterCorona(context: Context, posts: MutableList<Post>, loadNext: LoadNextPage) : RecyclerView.Adapter<AdapterCorona.MyViewHolder>(){

    var posts: MutableList<Post> = ArrayList()
    var context: Context
    var loadNextPage: LoadNextPage
    var multiTransformation = MultiTransformation(CenterCrop(), RoundedCorners(40))
    private val l: Preference<Int> = PreferenceModule.layoutMode


    init {
        this.posts = posts
        this.context = context
        this.loadNextPage = loadNext
    }

    enum class Inflater(val id: Int, @LayoutRes val element: Int){
        POSITION_ONE(0, R.layout.element_news_article_corona),
        POSITION_MRE(1, R.layout.element_news_article),
        POSITION_ALR(2, R.layout.element_corona_info),
        POSITION_CMP(3, R.layout.element_news_article_new)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(position != 0) {
            holder.tags!!.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            holder.tags!!.itemAnimator = DefaultItemAnimator()
            holder.tags!!.adapter = AdapterTag(posts[position-1].tags)
            setPost(holder, position-1)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val l = LayoutInflater.from(parent.context).inflate(Inflater.values()[viewType].element, parent, false) as LinearLayout
        return MyViewHolder(l)

    }

    override fun getItemViewType(position: Int): Int {
        if(position == 0) return 2
        else if(position == 1) return 0
        return if(l.get()==1) 3 else 1
    }


    override fun getItemCount(): Int {
        return posts.size + 1
    }



    inner class MyViewHolder(l: LinearLayout) : RecyclerView.ViewHolder(l) {

        var title: TextView? = l.findViewById(R.id.post_title)
        var articleImage: ImageView? = l.findViewById(R.id.post_image)
        var tags: RecyclerView? = l.findViewById(R.id.recycler_view_tag)
        var date: TextView? = l.findViewById(R.id.post_date_alternate)

        private var firstElementText: TextView? = l.findViewById(R.id.search_results_number)
        var imageLoaded = false
        var linearLayout: LinearLayout = l
        var disposable: Disposable? = null

    }



    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.disposable?.let { disposable -> disposable.dispose() }
        holder.disposable = RxView.clicks(holder.linearLayout)
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { empty ->
                if(holder.adapterPosition != 0) {
                    holder.articleImage!!.setTransitionName("article_header")
                    loadNextPage.onPostClicked(
                        posts[holder.adapterPosition-1],
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
        //if(position == 1) holder.firstElementText!!.text = context.getString(R.string.latest_corona)
        holder.title!!.text = posts[position].title
        holder.date!!.text = Utils.dateText(posts[position].date)//posts[position].date.toString("MMM d, yyyy")
        Glide.with(holder.articleImage!!)
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

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        holder.imageLoaded = true
                        return false
                    }
                }
            ).into(holder.articleImage!!)
        if(position > itemCount - 3) loadNextPage.bottomReached()

    }

    fun clear(){
        posts.clear()
        notifyDataSetChanged()
    }


}