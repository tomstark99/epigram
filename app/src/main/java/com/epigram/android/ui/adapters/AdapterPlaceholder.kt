package com.epigram.android.ui.adapters

import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epigram.android.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.w3c.dom.Text
import java.util.concurrent.TimeUnit

class AdapterPlaceholder : RecyclerView.Adapter<AdapterPlaceholder.MyViewHolder>(){

    private var placeholder_count = 3
    private var disposable_1: Disposable? = null
    private var disposable_2: Disposable? = null
    private var disposable_3: Disposable? = null

    fun clear() {
        placeholder_count = 0
        notifyDataSetChanged()
    }

    inner class MyViewHolder(l: LinearLayout) : RecyclerView.ViewHolder(l) {
        private var linearLayout: LinearLayout
        var p_1: TransitionDrawable
        var p_2: TransitionDrawable
        var p_3: TransitionDrawable

        init{
            p_1 = l.findViewById<TextView>(R.id.placeholder_1).background as TransitionDrawable
            p_2 = l.findViewById<TextView>(R.id.placeholder_2).background as TransitionDrawable
            p_3 = l.findViewById<TextView>(R.id.placeholder_3).background as TransitionDrawable
            linearLayout = l
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val l = LayoutInflater.from(parent.context).inflate(R.layout.element_place_holder, parent, false) as LinearLayout
        return MyViewHolder(l)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        while (animate) {
        holder.p_1.isCrossFadeEnabled = true
        disposable_1 = Observable.interval(200,1200, TimeUnit.MILLISECONDS)
                .repeat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    holder.p_1.startTransition(500)
                    Observable.timer(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                        .map{
                            holder.p_1.reverseTransition(500)
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                }
        disposable_2 = Observable.interval(300,1300, TimeUnit.MILLISECONDS)
                .repeat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    holder.p_2.startTransition(500)
                    Observable.timer(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                        .map {
                            holder.p_2.reverseTransition(500)
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                }
        disposable_3 = Observable.interval(400,1400, TimeUnit.MILLISECONDS)
                .repeat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    holder.p_3.startTransition(500)
                    Observable.timer(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                        .map{
                            holder.p_3.reverseTransition(500)
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                }
    }

    fun stopAnimation() {
        if(disposable_1 != null) disposable_1!!.dispose()
        if(disposable_2 != null) disposable_2!!.dispose()
        if(disposable_3 != null) disposable_3!!.dispose()
    }

    override fun getItemCount(): Int {
        return placeholder_count
    }

}