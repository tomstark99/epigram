package com.example.epigram;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.epigram.data.Post;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {

    private List<Post> posts = new ArrayList<>();
    private final MultiTransformation<Bitmap> multiTransformation;
    private LoadNextPage loadNextPage = null;

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public ImageView titleImage;
        public LinearLayout linearLayout;
        public TextView tag;
        // public TextView date; // for date on top of image
        public TextView dateAlt;
        public TextView tabTitle;

        public MyViewHolder(LinearLayout l){
            super(l);
            title = l.findViewById(R.id.post_title);
            tag = l.findViewById(R.id.tag_text);
            // date = l.findViewById(R.id.post_date); // for top of image
            dateAlt = l.findViewById(R.id.post_date_alternate);
            titleImage = l.findViewById(R.id.post_image);
            this.linearLayout = l;

            RxView.clicks(l).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(empty ->{
                titleImage.setTransitionName("article_header");
                loadNextPage.onPostClicked(posts.get(getAdapterPosition()), titleImage);
            });

        }
    }

    public MyAdapter2(List<Post> posts, LoadNextPage loadNext) {
        this.posts = posts;
        multiTransformation = new MultiTransformation<>(new CenterCrop(),new RoundedCorners(32));
        loadNextPage = loadNext;
    }

    public void addPosts(List<Post> postsNew){
        posts.addAll(postsNew); // adds the posts so it loads the old ones each time?
        notifyDataSetChanged();
    }

    @Override
    public MyAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LinearLayout l = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.element_news_article, parent, false);
        MyViewHolder vh = new MyViewHolder(l);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        holder.title.setText((posts.get(position).getTitle()));
        holder.tag.setText(posts.get(position).getTag());
        //holder.date.setText(posts.get(position).getDate().toString("MMM d, yyyy")); // date for on top of image
        holder.dateAlt.setText(posts.get(position).getDate().toString("MMM d, yyyy"));
        Glide.with(holder.titleImage).load(posts.get(position).getImage()).apply(RequestOptions.bitmapTransform(multiTransformation)).into(holder.titleImage);
        if(position == getItemCount()-1) loadNextPage.bottomReached();
    }

    @Override
    public int getItemCount(){
        return posts.size();
    }

    public interface LoadNextPage{
        public void bottomReached();

        public void onPostClicked(Post clicked, ImageView titleImage);
    }

}


