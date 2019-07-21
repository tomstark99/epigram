package com.example.epigram.data.api;

import androidx.annotation.Nullable;
import com.example.epigram.data.templates.PostTemplate;
import com.example.epigram.data.templates.SearchResult;
import com.example.epigram.data.templates.Wrapper;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EpigramService {

    @GET("ghost/api/v2/content/posts")
    Single<Wrapper<PostTemplate>> getPostsFilter(@Query("key") String key, @Query("include") String include, @Nullable @Query("filter") String fiter, @Query("limit") String limit, @Query("page") int page, @Query("order") String order);

    @GET("ghost/api/v2/content/posts")
    Single<Wrapper<PostTemplate>> getPostsBreak(@Query("key") String key, @Query("include") String include, @Nullable @Query("filter") String fiter, @Query("limit") String limit, @Query("order") String order);

    @GET("ghost/api/v2/content/posts")
    Single<Wrapper<SearchResult>> getSearchIDs(@Query("key") String key, @Query("include") String include, @Query("limit") String theLimit, @Nullable @Query("fields") String fields, @Query("page") int page, @Query("order") String order);

    @GET("ghost/api/v2/content/posts/{id}")
    Single<Wrapper<PostTemplate>> getPostFromNotification(@Path("id") String id, @Query("key") String key, @Query("include") String include);

}
