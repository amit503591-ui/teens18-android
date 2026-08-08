package com.teens18.app.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    private const val PREFS = "theme_prefs"
    private const val KEY_THEME = "theme_mode"
    const val MODE_LIGHT = 0
    const val MODE_DARK = 1
    const val MODE_SYSTEM = 2

    fun applyTheme(context: Context) {
        AppCompatDelegate.setDefaultNightMode(getThemeModeAsNightMode(getThemeMode(context)))
    }
    fun setThemeMode(context: Context, mode: Int) {
        getPrefs(context).edit().putInt(KEY_THEME, mode).apply()
        applyTheme(context)
    }
    fun getThemeMode(context: Context): Int = getPrefs(context).getInt(KEY_THEME, MODE_SYSTEM)
    fun getThemeModeAsNightMode(mode: Int) = when (mode) {
        MODE_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        MODE_DARK -> AppCompatDelegate.MODE_NIGHT_YES
        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    fun isDarkMode(context: Context): Boolean {
        return when (getThemeMode(context)) {
            MODE_DARK -> true
            MODE_LIGHT -> false
            else -> {
                val flags = context.resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK
                flags == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
    }
    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}