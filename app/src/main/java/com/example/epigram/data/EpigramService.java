package com.example.epigram.data;

import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EpigramService {

    @GET("ghost/api/v2/content/posts")
    Call<postTemplateWrapper> getPostsFilter(@Query("key") String key, @Query("include") String include, @Nullable @Query("filter") String fiter, @Query("limit") String limit, @Query("page") int page);

}
