package com.teens18.app.api

import com.teens18.app.model.Category
import com.teens18.app.model.Post
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("posts")
    fun getPosts(
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1,
        @Query("search") search: String? = null,
        @Query("categories") categories: String? = null,
        @Query("_embed") embed: Int = 1
    ): Call<List<Post>>
    @GET("posts/{id}")
    fun getPost(@Path("id") id: Int, @Query("_embed") embed: Int = 1): Call<Post>
    @GET("categories")
    fun getCategories(@Query("per_page") perPage: Int = 100): Call<List<Category>>
}