package com.epigram.android.data.managers

import com.epigram.android.data.model.Post
import io.reactivex.Single

interface ViewManager {
    fun getToken(): Single<String>
    fun validateToken(token: String): Single<Boolean>
    fun getViews(path: String, token: String): Single<String>
    fun getMostRead(count: Int, token: String): Single<List<Post>>
}
