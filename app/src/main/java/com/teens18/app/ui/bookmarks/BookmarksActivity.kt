package com.teens18.app.ui.bookmarks

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.teens18.app.data.PostRepository
import com.teens18.app.databinding.ActivityBookmarksBinding
import com.teens18.app.ui.detail.PostDetailActivity
import com.teens18.app.ui.posts.PostAdapter
import kotlinx.coroutines.launch

class BookmarksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarksBinding
    private lateinit var repository: PostRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Bookmarks"
        repository = PostRepository(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        load()
    }

    private fun load() {
        lifecycleScope.launch {
            val bookmarks = repository.getBookmarks()
            if (bookmarks.isEmpty()) { binding.emptyView.visibility = View.VISIBLE; binding.recyclerView.visibility = View.GONE }
            else {
                binding.emptyView.visibility = View.GONE; binding.recyclerView.visibility = View.VISIBLE
                binding.recyclerView.adapter = PostAdapter(bookmarks,
                    onClick = { startActivity(Intent(this@BookmarksActivity, PostDetailActivity::class.java).putExtra("post_id", it.id)) },
                    onBookmark = { lifecycleScope.launch { repository.toggleBookmark(it); load() } })
            }
        }
    }

    override fun onResume() { super.onResume(); load() }
    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}