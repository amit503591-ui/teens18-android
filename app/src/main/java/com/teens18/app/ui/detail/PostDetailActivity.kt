package com.teens18.app.ui.detail

import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.teens18.app.R
import com.teens18.app.ads.AdManager
import com.teens18.app.data.PostRepository
import com.teens18.app.databinding.ActivityPostDetailBinding
import com.teens18.app.util.DateUtils
import com.teens18.app.util.HtmlContentCleaner
import kotlinx.coroutines.launch

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDetailBinding
    private lateinit var repository: PostRepository
    private var postId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        repository = PostRepository(this)
        postId = intent.getIntExtra("post_id", -1)
        if (postId == -1) { finish(); return }
        loadPost()
        showInterstitialIfNeeded()
        AdManager.createBannerAd(this, binding.bottomAdContainer)
    }

    private fun loadPost() {
        lifecycleScope.launch {
            repository.getPost(postId).fold(
                onSuccess = { post ->
                    binding.collapsingToolbar.title = Html.fromHtml(post.title.rendered, Html.FROM_HTML_MODE_LEGACY).toString()
                    binding.dateText.text = DateUtils.formatDate(post.date)
                    binding.contentText.text = Html.fromHtml(HtmlContentCleaner.clean(post.content.rendered), Html.FROM_HTML_MODE_LEGACY)
                    if (!post.featuredImageUrl.isNullOrEmpty())
                        Glide.with(this@PostDetailActivity).load(post.featuredImageUrl).into(binding.headerImage)
                    val bookmarked = repository.isBookmarked(post.id)
                    binding.bookmarkFab.setImageResource(if (bookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_border)
                    binding.bookmarkFab.setOnClickListener {
                        lifecycleScope.launch {
                            val newState = repository.toggleBookmark(post)
                            binding.bookmarkFab.setImageResource(if (newState) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_border)
                            Toast.makeText(this@PostDetailActivity, if (newState) "Bookmarked" else "Removed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onFailure = { Toast.makeText(this@PostDetailActivity, "Error: ${it.message}", Toast.LENGTH_LONG).show(); finish() }
            )
        }
    }

    private fun showInterstitialIfNeeded() {
        val prefs = getSharedPreferences("ad_prefs", MODE_PRIVATE)
        val count = prefs.getInt("open_count", 0) + 1
        prefs.edit().putInt("open_count", count).apply()
        if (count % AdManager.getInterstitialFrequency(this) == 0)
            AdManager.showInterstitialAd(this) { }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}