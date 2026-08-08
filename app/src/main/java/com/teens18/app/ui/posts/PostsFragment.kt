package com.teens18.app.ui.posts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teens18.app.ads.AdManager
import com.teens18.app.data.PostRepository
import com.teens18.app.databinding.FragmentPostsBinding
import com.teens18.app.model.Post
import com.teens18.app.ui.detail.PostDetailActivity
import kotlinx.coroutines.launch

class PostsFragment : Fragment() {
    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private val posts = mutableListOf<Post>()
    private lateinit var adapter: PostAdapter
    private lateinit var repository: PostRepository
    private var page = 1
    private var isLoading = false
    private var hasMore = true
    private var categoryId: Int? = null
    private var searchQuery: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = PostRepository(requireContext())
        adapter = PostAdapter(posts,
            onClick = { startActivity(Intent(requireContext(), PostDetailActivity::class.java).putExtra("post_id", it.id)) },
            onBookmark = {
                lifecycleScope.launch {
                    val msg = if (repository.toggleBookmark(it)) "Bookmarked" else "Removed"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                }
            })
        adapter.setBannerFrequency(AdManager.getBannerFrequency(requireContext()))
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lm = rv.layoutManager as LinearLayoutManager
                if (!isLoading && hasMore && lm.findLastVisibleItemPosition() >= posts.size - 3) {
                    page++; loadPosts()
                }
            }
        })
        binding.swipeRefresh.setOnRefreshListener { reload() }
        reload()
    }

    private fun reload() {
        page = 1; hasMore = true; posts.clear(); adapter.notifyDataSetChanged(); loadPosts()
    }

    private fun loadPosts() {
        if (isLoading) return
        isLoading = true
        binding.progressBar.visibility = if (page == 1) View.VISIBLE else View.GONE
        lifecycleScope.launch {
            val result = repository.getPosts(page, categoryId = categoryId, search = searchQuery)
            isLoading = false
            binding.swipeRefresh.isRefreshing = false
            binding.progressBar.visibility = View.GONE
            result.fold(
                onSuccess = { newPosts ->
                    hasMore = newPosts.size >= 10
                    posts.addAll(newPosts)
                    adapter.notifyDataSetChanged()
                },
                onFailure = { Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_LONG).show() }
            )
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }

    companion object {
        fun newInstance(categoryId: Int? = null, searchQuery: String? = null) =
            PostsFragment().apply { this.categoryId = categoryId; this.searchQuery = searchQuery }
    }
}