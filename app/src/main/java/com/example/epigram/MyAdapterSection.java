package com.example.epigram;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.epigram.data.Post;
import com.jakewharton.rxbinding2.view.RxView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyAdapterSection extends RecyclerView.Adapter<MyAdapterSection.MyViewHolder> {

    public static int SEARCH_PAGE_INDEX = 100;
    public static int HOME_PAGE_INDEX = 0;

    public List<Post> posts = new ArrayList<>();
    private final MultiTransformation<Bitmap> multiTransformation;
    private LoadNextPage loadNextPage = null;

    private int resultTotal = 0;

    private String section;

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void setPostList(List<Post> checkSame) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return posts.size();
            }

            @Override
            public int getNewListSize() {
                return checkSame.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return posts.get(oldItemPosition) == checkSame.get(newItemPosition);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return posts.get(oldItemPosition).getId().equals(checkSame.get(newItemPosition).getId());
            }
        });
        posts = checkSame;
        diffResult.dispatchUpdatesTo(this);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public ImageView titleImage;
        public LinearLayout linearLayout;
        public TextView tag;
        // public TextView date; // for date on top of image
        public TextView dateAlt;
        //public TextView sectionTitle;
        public TextView sectionName;

        public boolean imageLoaded = false;

        private Disposable disposable = null;

        public MyViewHolder(LinearLayout l){
            super(l);
            //sectionName = l.findViewById(R.id.search_results_number);
            title = l.findViewById(R.id.post_title);
            tag = l.findViewById(R.id.tag_text);
            // date = l.findViewById(R.id.post_date); // for top of image
            dateAlt = l.findViewById(R.id.post_date_alternate);
            titleImage = l.findViewById(R.id.post_image);
            this.linearLayout = l;
            //sectionTitle = l.findViewById(R.id.section_text);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(holder.disposable != null) {
            holder.disposable.dispose();
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if(holder.disposable != null) {
            holder.disposable.dispose();
        }
        holder.disposable = RxView.clicks(holder.linearLayout).throttleFirst(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(empty -> {
            holder.titleImage.setTransitionName("article_header");

            loadNextPage.onPostClicked(posts.get(holder.getAdapterPosition()), holder.imageLoaded?holder.titleImage:null);
        });
    }

    public MyAdapterSection(List<Post> posts, LoadNextPage loadNext, String section) {
        this.posts = posts;
        multiTransformation = new MultiTransformation<>(new CenterCrop(),new RoundedCorners(40));
        loadNextPage = loadNext;
        this.section = section;
    }

    public void addPosts(List<Post> postsNew){

        List<Post> checkSame = new ArrayList<>(posts);
        checkSame.addAll(postsNew);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return posts.size();
            }

            @Override
            public int getNewListSize() {
                return checkSame.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return posts.get(oldItemPosition) == checkSame.get(newItemPosition);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return posts.get(oldItemPosition).getId().equals(checkSame.get(newItemPosition).getId());
            }
        });
        posts = checkSame;

        diffResult.dispatchUpdatesTo(this);
        //posts = new ListUtils().duplicatePost(new ArrayList<>(posts));
        //notifyDataSetChanged();
    }

    @Override
    public MyAdapterSection.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
//        if(viewType == 1){
//            LinearLayout l = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.element_news_article_first, parent, false);
//            MyViewHolder vh = new MyViewHolder(l);
//            return vh;
//        }
//        else {
            LinearLayout l = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.element_news_article, parent, false);
            MyViewHolder vh = new MyViewHolder(l);
            return vh;
//        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) return 1;
        else return 2;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        setPosts(holder, position);
    }

    public void setPosts(MyViewHolder holder, int position){
        if(position == 0){
            //holder.sectionName.setText(section);//Integer.toString(posts.size()));
        }
        List<String> tag = posts.get(position).getTags();
        tag.removeAll(Arrays.asList("featured top", "carousel", "one sidebar"));
        holder.title.setText((posts.get(position).getTitle()));
        holder.tag.setText(tag.get(0).toUpperCase());//posts.get(position).getTag());
        //holder.date.setText(posts.get(position).getDate().toString("MMM d, yyyy")); // date for on top of image
        holder.dateAlt.setText(posts.get(position).getDate().toString("MMM d, yyyy"));

        Glide.with(holder.titleImage).load(posts.get(position).getImage()).placeholder(R.drawable.placeholder_background).apply(RequestOptions.bitmapTransform(multiTransformation)).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.imageLoaded = false;
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.imageLoaded = true;
                return false;
            }
        }).into(holder.titleImage);
        if(position > getItemCount() - 2) loadNextPage.bottomReached();
    }

    @Override
    public int getItemCount(){
        return posts.size();
    }

    public interface LoadNextPage{
        public void bottomReached();

        public void onPostClicked(Post clicked, ImageView titleImage);
    }

    public void setResultTotal(int total){
        resultTotal = total;
        notifyItemChanged(0);
    }

}


