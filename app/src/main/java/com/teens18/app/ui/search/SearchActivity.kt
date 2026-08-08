package com.teens18.app.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.teens18.app.data.PostRepository
import com.teens18.app.databinding.ActivitySearchBinding
import com.teens18.app.ui.detail.PostDetailActivity
import com.teens18.app.ui.posts.PostAdapter
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var repository: PostRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        repository = PostRepository(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val initial = intent.getStringExtra("query") ?: ""
        if (initial.isNotEmpty()) { binding.searchView.setQuery(initial, true); search(initial) }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { if (!query.isNullOrEmpty()) search(query); return true }
            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun search(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = repository.getPosts(search = query)
            binding.progressBar.visibility = View.GONE
            result.fold(
                onSuccess = { posts ->
                    if (posts.isEmpty()) { binding.emptyView.visibility = View.VISIBLE; binding.emptyView.text = "No results for \"$query\"" }
                    else {
                        binding.emptyView.visibility = View.GONE
                        binding.recyclerView.adapter = PostAdapter(posts,
                            onClick = { startActivity(Intent(this@SearchActivity, PostDetailActivity::class.java).putExtra("post_id", it.id)) },
                            onBookmark = { lifecycleScope.launch { repository.toggleBookmark(it) } })
                    }
                },
                onFailure = { Toast.makeText(this@SearchActivity, "Error: ${it.message}", Toast.LENGTH_LONG).show() }
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}