package com.teens18.app.data

import android.content.Context
import com.teens18.app.api.ApiClient
import com.teens18.app.model.Post
import com.teens18.app.model.PostEntity
import com.teens18.app.util.NetworkUtil

class PostRepository(private val context: Context) {
    private val dao = AppDatabase.getInstance(context).postDao()

    suspend fun getPosts(page: Int = 1, perPage: Int = 10, search: String? = null, categoryId: Int? = null): Result<List<Post>> {
        return try {
            if (NetworkUtil.isOnline(context)) {
                val response = ApiClient.service.getPosts(perPage, page, search, categoryId?.toString()).execute()
                val posts = response.body() ?: emptyList()
                if (page == 1 && search.isNullOrEmpty() && categoryId == null) {
                    dao.insertAll(posts.map { PostEntity.fromPost(it) })
                }
                Result.success(posts)
            } else {
                val cached = if (page == 1) dao.getAllPosts() else emptyList()
                if (cached.isNotEmpty()) Result.success(cached.map { it.toPost() })
                else Result.failure(Exception("No internet and no cached data"))
            }
        } catch (e: Exception) {
            try {
                val cached = dao.getAllPosts()
                if (cached.isNotEmpty()) Result.success(cached.map { it.toPost() })
                else Result.failure(e)
            } catch (e2: Exception) { Result.failure(e) }
        }
    }

    suspend fun getPost(id: Int): Result<Post> {
        return try {
            if (NetworkUtil.isOnline(context)) {
                val post = ApiClient.service.getPost(id).execute().body()
                if (post != null) { dao.insert(PostEntity.fromPost(post)); Result.success(post) }
                else Result.failure(Exception("Post not found"))
            } else {
                val cached = dao.getPostById(id)
                if (cached != null) Result.success(cached.toPost())
                else Result.failure(Exception("Post not available offline"))
            }
        } catch (e: Exception) {
            val cached = dao.getPostById(id)
            if (cached != null) Result.success(cached.toPost()) else Result.failure(e)
        }
    }

    suspend fun toggleBookmark(post: Post): Boolean {
        val current = dao.isBookmarked(post.id) ?: false
        dao.insert(PostEntity.fromPost(post).copy(isBookmarked = !current))
        return !current
    }
    suspend fun getBookmarks(): List<Post> = dao.getBookmarks().map { it.toPost() }
    suspend fun isBookmarked(postId: Int): Boolean = dao.isBookmarked(postId) ?: false
    suspend fun clearCache() = dao.clearNonBookmarks()
}