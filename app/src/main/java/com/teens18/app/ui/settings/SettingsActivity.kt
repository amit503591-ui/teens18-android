package com.teens18.app.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.teens18.app.data.PostRepository
import com.teens18.app.databinding.ActivitySettingsBinding
import com.teens18.app.theme.ThemeManager
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        when (ThemeManager.getThemeMode(this)) {
            ThemeManager.MODE_LIGHT -> binding.radioLight.isChecked = true
            ThemeManager.MODE_DARK -> binding.radioDark.isChecked = true
            else -> binding.radioSystem.isChecked = true
        }
        binding.themeGroup.setOnCheckedChangeListener { _, id ->
            val mode = when (id) {
                binding.radioLight.id -> ThemeManager.MODE_LIGHT
                binding.radioDark.id -> ThemeManager.MODE_DARK
                else -> ThemeManager.MODE_SYSTEM
            }
            if (mode != ThemeManager.getThemeMode(this)) {
                ThemeManager.setThemeMode(this, mode); recreate()
            }
        }
        binding.clearCacheButton.setOnClickListener {
            lifecycleScope.launch {
                PostRepository(this@SettingsActivity).clearCache()
                Toast.makeText(this@SettingsActivity, "Cache cleared", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}