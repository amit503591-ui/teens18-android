package com.teens18.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val date: String, val slug: String, val link: String,
    val title: String, val content: String, val excerpt: String,
    val author: Int, val featuredImageUrl: String?,
    val categories: String,
    val cachedAt: Long = System.currentTimeMillis(),
    val isBookmarked: Boolean = false
) {
    fun toPost() = Post(id, date, slug, link, Rendered(title), Rendered(content),
        Rendered(excerpt), author, 0,
        if (categories.isEmpty()) emptyList() else categories.split(",").map { it.toInt() },
        featuredImageUrl)
    companion object {
        fun fromPost(p: Post) = PostEntity(p.id, p.date, p.slug, p.link,
            p.title.rendered, p.content.rendered, p.excerpt.rendered,
            p.author, p.featuredImageUrl, p.categories.joinToString(","))
    }
}