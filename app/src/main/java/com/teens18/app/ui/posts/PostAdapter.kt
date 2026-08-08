package com.teens18.app.ui.posts

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teens18.app.R
import com.teens18.app.ads.AdManager
import com.teens18.app.data.AppDatabase
import com.teens18.app.databinding.ItemPostBinding
import com.teens18.app.databinding.ItemNativeAdBinding
import com.teens18.app.model.Post
import com.teens18.app.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostAdapter(
    private val posts: List<Post>,
    private val onClick: (Post) -> Unit,
    private val onBookmark: (Post) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_POST = 0
    private val VIEW_AD = 1
    private val adPositions = mutableSetOf<Int>()
    private var freq = 5

    fun setBannerFrequency(f: Int) {
        freq = f
        adPositions.clear()
        for (i in posts.indices) if (i > 0 && i % f == 0) adPositions.add(i)
    }

    override fun getItemViewType(position: Int) = if (adPositions.contains(position)) VIEW_AD else VIEW_POST

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_AD) AdVH(ItemNativeAdBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else PostVH(ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AdVH) holder.bind()
        else if (holder is PostVH) holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    inner class PostVH(val b: ItemPostBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(post: Post) {
            val ctx = b.root.context
            b.titleText.text = Html.fromHtml(post.title.rendered, Html.FROM_HTML_MODE_LEGACY)
            b.excerptText.text = Html.fromHtml(post.excerpt.rendered, Html.FROM_HTML_MODE_LEGACY)
            b.dateText.text = DateUtils.formatDate(post.date)
            if (!post.featuredImageUrl.isNullOrEmpty()) {
                Glide.with(ctx).load(post.featuredImageUrl).placeholder(R.drawable.placeholder).into(b.featuredImage)
                b.featuredImage.visibility = View.VISIBLE
            } else b.featuredImage.visibility = View.GONE
            CoroutineScope(Dispatchers.IO).launch {
                val bookmarked = AppDatabase.getInstance(ctx).postDao().isBookmarked(post.id) ?: false
                b.bookmarkIcon.setImageResource(if (bookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_border)
            }
            b.root.setOnClickListener { onClick(post) }
            b.bookmarkIcon.setOnClickListener { onBookmark(post) }
        }
    }

    inner class AdVH(val b: ItemNativeAdBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind() { AdManager.createNativeAd(b.root.context, b.nativeAdContainer) }
    }
}