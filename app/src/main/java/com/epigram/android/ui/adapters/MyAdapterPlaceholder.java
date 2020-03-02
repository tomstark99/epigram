package com.epigram.android.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.epigram.android.R;

public class MyAdapterPlaceholder extends RecyclerView.Adapter<MyAdapterPlaceholder.MyViewHolder> {

    private int placeholderCount = 3;

    public void clear() {
        placeholderCount = 0;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout linearLayout;
        public MyViewHolder(LinearLayout l){
            super(l);
            this.linearLayout = l;
        }
    }

    @Override
    public MyAdapterPlaceholder.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LinearLayout l = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.element_place_holder, parent, false);
        MyViewHolder vh = new MyViewHolder(l);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
    }

    @Override
    public int getItemCount(){
        return placeholderCount;
    }
}