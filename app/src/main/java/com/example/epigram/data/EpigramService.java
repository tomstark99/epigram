package com.example.epigram.data;

import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EpigramService {

    @GET("ghost/api/v2/content/posts")
    Call<Wrapper<PostTemplate>> getPostsFilter(@Query("key") String key, @Query("include") String include, @Nullable @Query("filter") String fiter, @Query("limit") String limit, @Query("page") int page);

    @GET("ghost/api/v2/content/posts")
    Call<Wrapper<SearchResult>> getSearchIDs(@Query("key") String key, @Query("include") String include, @Query("limit") String theLimit, @Nullable @Query("fields") String fields);

    @GET("ghost/api/v2/content/posts")
    Call<Wrapper<PostTemplate>> getPostsFromSearch(@Query("key") String key,@Query("include") String include ,@Query("limit") String theLimit, @Nullable @Query("filter") String filter);

}
