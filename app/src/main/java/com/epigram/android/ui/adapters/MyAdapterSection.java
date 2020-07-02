package com.epigram.android.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.epigram.android.R;
import com.epigram.android.data.model.Post;
import com.jakewharton.rxbinding2.view.RxView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyAdapterSection extends RecyclerView.Adapter<MyAdapterSection.MyViewHolder> {

    public List<Post> posts;
    private final MultiTransformation<Bitmap> multiTransformation;
    private LoadNextPage loadNextPage;
    private Context context;
    private String section;

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
        public RecyclerView tags;
        public TextView dateAlt;

        public boolean imageLoaded = false;

        private Disposable disposable = null;

        public MyViewHolder(LinearLayout l){
            super(l);
            title = l.findViewById(R.id.post_title);
            tags = l.findViewById(R.id.recycler_view_tag);
            dateAlt = l.findViewById(R.id.post_date_alternate);
            titleImage = l.findViewById(R.id.post_image);
            this.linearLayout = l;
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

    public MyAdapterSection(Context context, List<Post> posts, LoadNextPage loadNext, String section) {
        this.context = context;
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
    }

    @Override
    public MyAdapterSection.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LinearLayout l = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.element_news_article, parent, false);
            MyViewHolder vh = new MyViewHolder(l);
            return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        holder.tags.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.tags.setItemAnimator(new DefaultItemAnimator());
        holder.tags.setAdapter(new MyAdapterTag(posts.get(position).getTags()));
        setPosts(holder, position);
    }

    public void setPosts(MyViewHolder holder, int position){
        if (position == 0) {
            float scale = context.getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (6*scale + 0.5f);
            holder.titleImage.setPadding(0,dpAsPixels, 0, 0);
        }
        holder.title.setText((posts.get(position).getTitle()));
        holder.dateAlt.setText(posts.get(position).getDate().toString("MMM d, yyyy"));
        List<String> tag = posts.get(position).getTags();
        tag.removeAll(Arrays.asList("featured top", "carousel", "one sidebar"));
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
        void bottomReached();
        void onPostClicked(Post clicked, ImageView titleImage);
    }
}


