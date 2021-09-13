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
import com.epigram.android.data.arch.utils.SnapHelperOne
import com.epigram.android.data.arch.utils.Utils
import com.epigram.android.data.model.Post
import com.f2prateek.rx.preferences2.Preference
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class AdapterArticlesHome(context: Context, posts: MutableList<Post>, var breakingPosts: MutableList<Post>, loadNext: LoadNextPage, position: Int) : RecyclerView.Adapter<AdapterArticlesHome.MyViewHolder>(){

    var posts: MutableList<Post> = ArrayList()
    var context: Context
    var loadNextPage: LoadNextPage
    var multiTransformation = MultiTransformation(CenterCrop(), RoundedCorners(40))
    var pageIndex: Int = 0
    private val c: Preference<Int> = PreferenceModule.counter
    private val l: Preference<Int> = PreferenceModule.layoutMode
    private var mask = displayMask()
    var only_one = true

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
        POSITION_CMP(3, R.layout.element_news_article_new)
//        POSITION_MAS(4, R.layout.element_corona_mask)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        if (position == 0) {
//            if(mask) {
//                mask = false
//                holder.maskLayout!!.visibility = View.VISIBLE
////                holder.staySafe!!.setBackgroundResource(0) // ACTIVATE THESE TWO
////                holder.staySafe!!.setTextColor(context.getColor(R.color.black)) // TWO
//                holder.maskCLoseIc!!.animate().rotationBy(180f).start()
//            }
//        }
        if (position == 0) {
            val snapHelper = SnapHelperOne()
            holder.breaking!!.onFlingListener = null
            snapHelper.attachToRecyclerView(holder.breaking!!)
            holder.breaking!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.breaking!!.adapter = AdapterBreaking(context, breakingPosts, loadNextPage)
        }
        else if(position > 0) {
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
//        if(position == 0) {
//            return 4
//        }
        if(position == 0) return 0
        else if(position == 1) return 1
        return if(l.get() == 1) 3 else 2
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
        var maskLayout: LinearLayout?
        var maskClose: LinearLayout?
        var maskCLoseIc: ImageView?
        var staySafe: TextView?
        var disposable: Disposable? = null

        init {
            title = l.findViewById(R.id.post_title)
            articleImage = l.findViewById(R.id.post_image)
            date = l.findViewById(R.id.post_date_alternate)
            tags = l.findViewById(R.id.recycler_view_tag)
            breaking = l.findViewById(R.id.recycler_breaking)
            firstElementText = l.findViewById(R.id.search_results_number)
            maskLayout = l.findViewById(R.id.mask)
            maskClose = l.findViewById(R.id.mask_close)
            maskCLoseIc = l.findViewById(R.id.mask_close_ic)
            staySafe = l.findViewById(R.id.stay_safe)

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
                if (holder.adapterPosition > 0) {
                    holder.articleImage!!.setTransitionName("article_header")
                    loadNextPage.onPostClicked(
                        posts[holder.adapterPosition-1],
                        if (holder.imageLoaded) holder.articleImage else null
                    )
                }
            }
//        if (holder.adapterPosition == 0) {
//            RxView.clicks(holder.maskClose!!)
//                .throttleFirst(500, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { empty ->
//                    if(holder.maskLayout!!.visibility == View.VISIBLE) {
//                        holder.maskLayout!!.visibility = View.GONE
////                        holder.staySafe!!.setBackgroundResource(R.drawable.tab_c_background) // ACTIVATE THESE TWO
////                        holder.staySafe!!.setTextColor(context.getColor(R.color.red_to_white)) // TWO
//                        holder.maskCLoseIc!!.animate().rotationBy(-180f).start()
////                        holder.maskLayout!!.visibility = View.GONE
////                        holder.staySafe!!.visibility = View.VISIBLE
////                        holder.maskCLoseIc!!.animate().rotationBy(-180f).start()
//                    } else {
//                        holder.maskLayout!!.visibility = View.VISIBLE
////                        holder.staySafe!!.setBackgroundResource(0) // ACTIVATE THESE TWO
////                        holder.staySafe!!.setTextColor(context.getColor(R.color.black)) // TWO
//                        holder.maskCLoseIc!!.animate().rotationBy(180f).start()
////                        holder.staySafe!!.visibility = View.GONE
////                        holder.maskLayout!!.visibility = View.VISIBLE
////                        holder.maskCLoseIc!!.animate().rotationBy(180f).start()
//                    }
//                    if(only_one && c.get() == 0) {
//                        only_one = false
//                        c.set(c.get() + 2)
//                    }
//                }
//        }
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.disposable?.let { disposable -> disposable.dispose() }
    }

    fun addPosts(newPosts: List<Post>){
//        posts.remove(posts.last())
        val posts2 = posts
        posts2.addAll(newPosts)
        Observable.fromIterable(posts2)
            .distinct({it.id})
            .toList()
            .subscribe { it -> posts = it}
        notifyDataSetChanged()
    }

    fun setPost(holder: MyViewHolder, position: Int){
        if(position == 0 && posts[position].date.plusWeeks(1).isBeforeNow && posts[position].tags.second.orEmpty().contains("breaking-news")) {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams =  RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1)
        } else {
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams =  RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        holder.title!!.text = if (posts[position].title.split(" ").size >= 15) posts[position].title.split(" ").take(15).joinToString(" ") + "..." else posts[position].title
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

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        holder.imageLoaded = true
                        return false
                    }
                }
            ).into(holder.articleImage!!)

        if(position > itemCount - 3) {
//            posts.add
            loadNextPage.bottomReached()
        }

    }

    fun clear(){
        posts.clear()
        notifyDataSetChanged()
    }

    fun displayMask() : Boolean {
        var temp = c.get()
        return if(temp == 0) true else false
    }

}