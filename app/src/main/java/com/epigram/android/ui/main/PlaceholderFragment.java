package com.example.epigram.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.epigram.*;
import com.example.epigram.data.Layout;
import com.example.epigram.data.Post;
import com.example.epigram.data.PostManager;
import com.example.epigram.ui.adapters.AdapterArticles;
import com.example.epigram.ui.adapters.MyAdapterPlaceholder;
import com.example.epigram.ui.article.ArticleActivity;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements AdapterArticles.LoadNextPage{

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_HAS_BREAKING = "has_breaking.bool";

    private int FIRST_INDEX = 1;

    private PostManager pManager = new PostManager();

    private AdapterArticles adapter2 = null;
    private int nextPage = FIRST_INDEX;
    private boolean loaded = false;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private List<Post> breaking = new ArrayList<>();

    private int pageIndex;


    public static PlaceholderFragment newInstance(int index, int position) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        bundle.putBoolean(ARG_HAS_BREAKING, position == 0);
        fragment.setArguments(bundle);
        fragment.pageIndex = position;
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.my_recycler_view);

        LinearLayoutManager layoutManager = new Layout(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setNestedScrollingEnabled(false);

        if(recyclerView.getAdapter() == null) {
            if(adapter2 == null) {
                recyclerView.setAdapter(new MyAdapterPlaceholder());
            }
            else{
                recyclerView.setAdapter(adapter2);
            }
        }
        load();

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorAccentHint);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter2 == null && !(recyclerView.getAdapter() instanceof MyAdapterPlaceholder)){
                    // app crashes if there is no adapter e.g. if there is no internet connection
                }
                else {
                    nextPage = FIRST_INDEX;
                    //if (adapter2 != null) adapter2.clear();
                    load();
                }
            }
        });



    }

    public void loadPage(){
        getView().findViewById(R.id.tab_something_wrong).setVisibility(View.GONE);
        int tag = getArguments().getInt(ARG_SECTION_NUMBER);
        Single<Pair<List<Post>, List<Post>>> single = null;
        if(pageIndex == 0 && nextPage == 1){
            single = Single.zip(pManager.getPosts(nextPage, getString(tag)), pManager.getPostsBreaking(), (posts, posts2) -> new Pair<>(posts, posts2));
        }
        else {
            single = pManager.getPosts(nextPage, getString(tag)).map( posts ->
                new Pair<>(posts, new ArrayList<>())
            );
        }
                single//.retry(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( posts-> {
                            loaded = true;
                            swipeRefresh.setRefreshing(false);
                            if(!posts.second.isEmpty()){
                                posts.first.removeAll(posts.second);
                                posts.first.add(0, posts.second.get(0));
                            }
                            nextPage++;
                            if (adapter2 == null) {
                                //adapter2 = new MyAdapterArticles(this.getContext(), posts.first, PlaceholderFragment.this, pageIndex);
                                recyclerView.setAdapter(adapter2);
                            }
                            else {
                                if(nextPage == FIRST_INDEX + 1) adapter2.clear();
                                adapter2.addPosts(posts.first);
                            }
                        }
                        ,e-> {Log.e("e", "e", e);
                            if(recyclerView.getAdapter() instanceof MyAdapterPlaceholder) {
                                ((MyAdapterPlaceholder) recyclerView.getAdapter()).clear();
                                getView().findViewById(R.id.tab_something_wrong).setVisibility(View.VISIBLE);
                            }

                            swipeRefresh.setRefreshing(false);});
    }



    public void load(){
        getView().findViewById(R.id.tab_something_wrong).setVisibility(View.GONE);
        int tag = getArguments().getInt(ARG_SECTION_NUMBER);
        Single<Pair<List<Post>, List<Post>>> single = null;
        if(pageIndex == 0 && nextPage == 1){
            single = Single.zip(pManager.getPosts(nextPage, getString(tag)), pManager.getPostsBreaking(), (posts, posts2) -> new Pair<>(posts, posts2));
        }
        else {
            single = pManager.getPosts(nextPage, getString(tag)).map( posts ->
                    new Pair<>(posts, new ArrayList<>())
            );
        }
        single//.retry(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( posts-> {
                            loaded = true;
                            swipeRefresh.setRefreshing(false);
                            if(!posts.second.isEmpty()){
                                posts.first.removeAll(posts.second);
                                posts.first.add(0, posts.second.get(0));
                            }
                            nextPage++;
                            if (adapter2 == null) {
                                adapter2 = new AdapterArticles(this.getContext(), posts.first, PlaceholderFragment.this, pageIndex, getArguments().getBoolean(ARG_HAS_BREAKING));
                                recyclerView.setAdapter(adapter2);
                            }
                            else {
                                if(nextPage == FIRST_INDEX + 1) adapter2.clear();
                                adapter2.addPosts(posts.first);
                            }
                        }
                        ,e-> {Log.e("e", "e", e);
                            if(recyclerView.getAdapter() instanceof MyAdapterPlaceholder) {
                                ((MyAdapterPlaceholder) recyclerView.getAdapter()).clear();
                                getView().findViewById(R.id.tab_something_wrong).setVisibility(View.VISIBLE);
                            }

                            swipeRefresh.setRefreshing(false);});
    }


    @Override
    public void bottomReached() {
        if(!loaded) return;
        loaded = false;
        load();

    }

    @Override
    public void onPostClicked(Post clicked, ImageView imageView) {
        if (!clicked.getTags().isEmpty() && clicked.getTags().get(0).equals("ADVERT")){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "https://play.google.com/store/apps/details?id=co.uk.vcars.bristol"));
            //intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=co.uk.vcars.bristol"));
            startActivity(intent);
        }
        else {
            if (imageView != null) {
                ArticleActivity.Companion.start(getActivity(), clicked, imageView);
            } else {
                ArticleActivity.Companion.start(getActivity(), clicked);
            }
        }
    }

    public void reselected() {
        recyclerView.smoothScrollToPosition(0);
    }
}