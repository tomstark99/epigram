package com.example.epigram.ui.main;

import android.os.Bundle;
import android.util.Log;
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
import com.example.epigram.ArticleActivity;
import com.example.epigram.MyAdapterArticles;
import com.example.epigram.MyAdapterPlaceholder;
import com.example.epigram.R;
import com.example.epigram.data.Layout;
import com.example.epigram.data.Post;
import com.example.epigram.data.PostManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements MyAdapterArticles.LoadNextPage {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private int FIRST_INDEX = 1;

    private PostManager pManager = new PostManager();

    private MyAdapterArticles adapter2 = null;
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
        loadPage();

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter2 == null){
                    // app crashes if there is no adapter e.g. if there is no internet connection
                }
                else {
                    nextPage = FIRST_INDEX;
                    adapter2.clear();
                    loadPage();
                }
            }
        });



    }

    public void loadPage(){
        int tag = getArguments().getInt(ARG_SECTION_NUMBER);
        getBreaking();
        pManager.getPosts(nextPage, getString(tag))
                .retry()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( posts-> {
                            loaded = true;
                            swipeRefresh.setRefreshing(false);
                            if(pageIndex == 0 && nextPage == 1){
                                posts.add(0, breaking.get(0));
                            }
                            nextPage++;
                            if (adapter2 == null) {
                                adapter2 = new MyAdapterArticles(posts, PlaceholderFragment.this, pageIndex);
                                recyclerView.setAdapter(adapter2);
                            }
                            else {
                                adapter2.addPosts(posts);
                            }
                        }
                        ,e-> Log.e("e", "e", e));
    }

    public void getBreaking(){
        if(breaking.isEmpty()) {
            pManager.getPostsBreaking()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(posts -> {
                                breaking.add(0, posts.get(0));
                                breaking = posts;
                            }
                            , e -> Log.e("e", "e", e));
        }
    }

    @Override
    public void bottomReached() {
        if(!loaded) return;
        loaded = false;
        loadPage();

    }

    @Override
    public void onPostClicked(Post clicked, ImageView imageView) {
        if(imageView != null) {
            ArticleActivity.Companion.start(getActivity(), clicked, imageView);
        }
        else{
            ArticleActivity.Companion.start(getActivity(), clicked);
        }
    }

    public void reselected() {
        recyclerView.smoothScrollToPosition(0);
    }
}