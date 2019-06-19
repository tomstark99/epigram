package com.example.epigram;

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
import com.example.epigram.data.Layout;
import com.example.epigram.data.Post;
import com.example.epigram.data.PostManager;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SearchActivity extends AppCompatActivity implements MyAdapterArticles.LoadNextPage {

    private PostManager pManager = new PostManager();
    private MyAdapterArticles adapterArticles = new MyAdapterArticles(new ArrayList<>(), this);
    private RecyclerView recyclerView;

    private EditText searchText = null;

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

        Observable<Object> searchButtonPress = RxView.clicks(findViewById(R.id.search_button_in_search_activity)).doOnNext(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Utils.hideKeyboard(SearchActivity.this);
            }
        });

        Observable<Integer> searchKeyboardPress = RxTextView.editorActions(searchText).doOnNext(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Utils.hideKeyboard(SearchActivity.this);
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
                return charSequence.length() > 2;
            }
        }).throttleFirst(200, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String charSequence) throws Exception {
                allPostTitles(charSequence);
            }
        });

    }


    public void allPostTitles(String searchQuery){
        pManager.getPostTitles(searchQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( posts-> {
                            adapterArticles.clear();
                            adapterArticles.addPosts(posts);
                        }
                        ,e-> Log.e("e", "e", e));
    }


    public static void start(Context context){
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void bottomReached() {

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
