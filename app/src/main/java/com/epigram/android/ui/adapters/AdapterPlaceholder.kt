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
    private var disposable: Disposable? = null

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
        val i = (position + 2) * 200
            holder.p_1.isCrossFadeEnabled = true
            disposable = Observable.interval(i.toLong(),1500, TimeUnit.MILLISECONDS)
                .repeat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    holder.p_1.startTransition(500)
                    holder.p_2.startTransition(500)
                    holder.p_3.startTransition(500)
                    Observable.timer(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                        .map{
                            holder.p_1.reverseTransition(500)
                            holder.p_2.reverseTransition(500)
                            holder.p_3.reverseTransition(500)
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                }
    }

    fun stopAnimation(){
        if(disposable != null) disposable!!.dispose()
    }

    override fun getItemCount(): Int {
        return placeholder_count
    }




}