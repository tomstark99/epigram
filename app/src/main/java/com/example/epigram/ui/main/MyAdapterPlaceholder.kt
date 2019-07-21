package com.example.epigram.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.epigram.R
import com.example.epigram.ui.main.MyAdapterPlaceholder.MyViewHolder

class MyAdapterPlaceholder : RecyclerView.Adapter<MyAdapterPlaceholder.MyViewHolder>() {

    private var placeholderCount = 3

    fun clear() {
        placeholderCount = 0
        notifyDataSetChanged()
    }

    class MyViewHolder(var linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapterPlaceholder.MyViewHolder {
        val l =
            LayoutInflater.from(parent.context).inflate(R.layout.element_place_holder, parent, false) as LinearLayout
        return MyViewHolder(l)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {}

    override fun getItemCount(): Int {
        return placeholderCount
    }
}
