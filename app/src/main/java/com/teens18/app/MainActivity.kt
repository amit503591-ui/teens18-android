package com.teens18.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.teens18.app.api.ApiClient
import com.teens18.app.databinding.ActivityMainBinding
import com.teens18.app.model.Category
import com.teens18.app.theme.ThemeManager
import com.teens18.app.ui.bookmarks.BookmarksActivity
import com.teens18.app.ui.posts.PostsFragment
import com.teens18.app.ui.search.SearchActivity
import com.teens18.app.ui.settings.SettingsActivity
import com.teens18.app.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val categories = mutableListOf<Category?>(null)
    private val fragments = mutableListOf<PostsFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Teens18"
        if (!NetworkUtil.isOnline(this)) {
            Toast.makeText(this, "Offline mode - showing cached posts", Toast.LENGTH_LONG).show()
        }
        setupThemeToggle()
        loadCategories()
    }

    private fun setupThemeToggle() {
        val icon = if (ThemeManager.isDarkMode(this)) R.drawable.ic_sun else R.drawable.ic_moon
        binding.fabTheme.setImageResource(icon)
        binding.fabTheme.setOnClickListener {
            val current = ThemeManager.getThemeMode(this)
            val newMode = if (current == ThemeManager.MODE_DARK) ThemeManager.MODE_LIGHT else ThemeManager.MODE_DARK
            ThemeManager.setThemeMode(this, newMode)
            recreate()
        }
    }

    private fun loadCategories() {
        if (!NetworkUtil.isOnline(this)) { setupTabs(); return }
        ApiClient.service.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    categories.clear()
                    categories.add(null)
                    categories.addAll(list)
                    setupTabs()
                }
            }
            override fun onFailure(call: Call<List<Category>>, t: Throwable) { setupTabs() }
        })
    }

    private fun setupTabs() {
        if (fragments.isEmpty()) {
            fragments.add(PostsFragment.newInstance())
            categories.drop(1).forEach { fragments.add(PostsFragment.newInstance(categoryId = it?.id)) }
        }
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = categories[position]?.name ?: "All"
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search articles..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java)
                    .putExtra("query", query))
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean = false
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_bookmarks -> { startActivity(Intent(this, BookmarksActivity::class.java)); true }
            R.id.action_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}