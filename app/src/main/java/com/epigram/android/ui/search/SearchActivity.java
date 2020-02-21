package com.epigram.android.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.epigram.android.ui.article.ArticleActivity;
import com.epigram.android.ui.adapters.MyAdapterArticles;
import com.epigram.android.R;
import com.epigram.android.arch.utils.Utils;
import com.epigram.android.data.Layout;
import com.epigram.android.data.Post;
import com.epigram.android.data.PostManager;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import kotlin.Triple;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.epigram.android.ui.adapters.MyAdapterArticles.SEARCH_PAGE_INDEX;

public class SearchActivity extends AppCompatActivity implements MyAdapterArticles.LoadNextPage {

    private PostManager pManager = new PostManager();
    private MyAdapterArticles adapterArticles = new MyAdapterArticles(this, new ArrayList<>(), this, SEARCH_PAGE_INDEX);
    //private AdapterSearch adapterArticles = new AdapterSearch(this, new ArrayList<>(), this);
    private RecyclerView recyclerView;

    private EditText searchText = null;
    private SwipeRefreshLayout ref = null;

    private int FIRST_INDEX = 1;
    private int nextPage = FIRST_INDEX;
    private boolean loaded = false;

    private int retry = 0;

    private String latestSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchText = findViewById(R.id.search_query);

        recyclerView = findViewById(R.id.recycler_view_search);

        LinearLayoutManager layoutManager = new Layout(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterArticles);
        recyclerView.setNestedScrollingEnabled(false);

        findViewById(R.id.search_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.search_button_in_search_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchText.getText().length() > 2) {
                    allPostTitles(searchText.getText().toString());
                }
            }
        });
        ref = findViewById(R.id.swipe_refresh);
        ref.setColorSchemeResources(R.color.colorAccent, R.color.colorAccentHint);
        ref.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(searchText.getText().length() > 2) {
                    allPostTitles(searchText.getText().toString());
                } else {
                    ref.setRefreshing(false);
                }
            }
        });

        Observable<Object> searchButtonPress = RxView.clicks(findViewById(R.id.search_button_in_search_activity)).doOnNext(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if(recyclerView.hasFocus()){
                    //findViewById(R.id.search_query).requestFocus();
                    recyclerView.smoothScrollToPosition(0);
                    //Utils.showKeyboard(findViewById(R.id.search_query), SearchActivity.this);
                }
                else {
                    Utils.hideKeyboard(SearchActivity.this);
                    recyclerView.requestFocus();
                }
            }
        });

        Observable<Integer> searchKeyboardPress = RxTextView.editorActions(searchText).doOnNext(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Utils.hideKeyboard(SearchActivity.this);
                recyclerView.requestFocus();
            }
        });

        Observable.merge(searchButtonPress, searchKeyboardPress, RxTextView.textChanges(searchText)).map(new Function<Object, String>() {
            @Override
            public String apply(Object o) throws Exception {
                return searchText.getText().toString();
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String charSequence) throws Exception {
                if(charSequence.length() == 0){
                    findViewById(R.id.search_no_result).setVisibility(View.GONE);
                    findViewById(R.id.search_progress).setVisibility(View.GONE);
                    findViewById(R.id.search_something_wrong).setVisibility(View.GONE);
                    adapterArticles.clear();
                    findViewById(R.id.search_placeholder).setVisibility(View.VISIBLE);
                }
                return charSequence.length() > 2;
            }
        }).throttleFirst(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String charSequence) throws Exception {
                findViewById(R.id.search_no_result).setVisibility(View.GONE);
                findViewById(R.id.search_placeholder).setVisibility(View.GONE);
                findViewById(R.id.search_something_wrong).setVisibility(View.GONE);
                if(adapterArticles.posts.isEmpty()) {
                    findViewById(R.id.search_progress).setVisibility(View.VISIBLE);
                }
                allPostTitles(charSequence);
            }
        });

    }


    public void allPostTitles(String searchQuery){
        findViewById(R.id.search_something_wrong).setVisibility(View.GONE);
        retry++;
        if(!searchQuery.equals(latestSearch)){

            nextPage = 1;
            latestSearch = searchQuery;
            retry = 0;
        }
        Single.zip(pManager.getPostTitles(nextPage, latestSearch), pManager.getSearchTotal(latestSearch), (stringListPair, integer) -> new Triple<>(integer, stringListPair.getFirst(), stringListPair.getSecond())).retry(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( triple -> {
                            if(!triple.getSecond().equals(latestSearch)) return;
                            loaded = true;
                            ref.setRefreshing(false);
                            if(triple.getThird().size() == 0) {
                                if(retry <= 10) {
                                    allPostTitles(searchQuery);
                                }
                                if(retry == 11 && adapterArticles.posts.isEmpty()){
                                    if(!(findViewById(R.id.search_placeholder).getVisibility() == View.VISIBLE)) {
                                        findViewById(R.id.search_progress).setVisibility(View.GONE);
                                        findViewById(R.id.search_no_result).setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                            else {
                                retry = 0;
                                adapterArticles.setResultTotal(triple.getFirst());
                                findViewById(R.id.search_progress).setVisibility(View.GONE);
                                if(nextPage == 1){
                                    adapterArticles.initPosts(triple.getThird());
                                }
                                else {
                                    adapterArticles.addPosts(triple.getThird());
                                }
                            }
                            nextPage++;
                        }
                        ,e-> {
                                findViewById(R.id.search_something_wrong).setVisibility(View.VISIBLE);
                                findViewById(R.id.search_progress).setVisibility(View.GONE);
                                Log.e("e", "e", e);
                });
    }


    public static void start(Context context){
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void bottomReached() {
        if(!loaded) return;
        loaded = false;
        recyclerView.post(new Runnable() {
                              @Override
                              public void run() {
                                  allPostTitles(searchText.getText().toString());
                              }
                          });
    }

    @Override
    public void onPostClicked(Post clicked, ImageView titleImage) {
        if(titleImage != null) {
            ArticleActivity.Companion.start(this, clicked, titleImage);
        }
        else{
            ArticleActivity.Companion.start(this, clicked);
        }
    }
}
