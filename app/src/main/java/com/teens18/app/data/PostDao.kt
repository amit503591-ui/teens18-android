package com.teens18.app.data

import androidx.room.*
import com.teens18.app.model.PostEntity

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(posts: List<PostEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(post: PostEntity)
    @Query("SELECT * FROM posts ORDER BY id DESC") suspend fun getAllPosts(): List<PostEntity>
    @Query("SELECT * FROM posts WHERE id = :id") suspend fun getPostById(id: Int): PostEntity?
    @Query("SELECT * FROM posts WHERE isBookmarked = 1 ORDER BY id DESC") suspend fun getBookmarks(): List<PostEntity>
    @Query("UPDATE posts SET isBookmarked = :bookmarked WHERE id = :id") suspend fun setBookmark(id: Int, bookmarked: Boolean)
    @Query("SELECT isBookmarked FROM posts WHERE id = :id") suspend fun isBookmarked(id: Int): Boolean?
    @Query("DELETE FROM posts WHERE isBookmarked = 0") suspend fun clearNonBookmarks()
}