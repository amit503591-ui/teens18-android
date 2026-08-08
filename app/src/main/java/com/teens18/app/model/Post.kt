package com.teens18.app.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    @SerializedName("id") val id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("link") val link: String,
    @SerializedName("title") val title: Rendered,
    @SerializedName("content") val content: Rendered,
    @SerializedName("excerpt") val excerpt: Rendered,
    @SerializedName("author") val author: Int,
    @SerializedName("featured_media") val featuredMedia: Int,
    @SerializedName("categories") val categories: List<Int> = emptyList(),
    @SerializedName("jetpack_featured_media_url") val featuredImageUrl: String? = null
) : Parcelable

@Parcelize
data class Rendered(@SerializedName("rendered") val rendered: String) : Parcelable