package com.example.epigram.data;

import androidx.annotation.Nullable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EpigramService {

    @GET("ghost/api/v2/content/posts")
    Single<Wrapper<PostTemplate>> getPostsFilter(@Query("key") String key, @Query("include") String include, @Nullable @Query("filter") String fiter, @Query("limit") String limit, @Query("page") int page, @Query("order") String order);

    @GET("ghost/api/v2/content/posts")
    Single<Wrapper<SearchResult>> getSearchIDs(@Query("key") String key, @Query("include") String include, @Query("limit") String theLimit, @Nullable @Query("fields") String fields, @Query("page") int page, @Query("order") String order);

}
