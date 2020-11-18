package com.epigram.android.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.epigram.android.R

class AdapterPlaceholder : RecyclerView.Adapter<AdapterPlaceholder.MyViewHolder>(){

    private var placeholder_count = 3

    fun clear() {
        placeholder_count = 0
        notifyDataSetChanged()
    }

    inner class MyViewHolder(var l: LinearLayout) : RecyclerView.ViewHolder(l) {
        private var linearLayout = l
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val l = LayoutInflater.from(parent.context).inflate(R.layout.element_place_holder, parent, false) as LinearLayout
        return MyViewHolder(l)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return placeholder_count
    }




}