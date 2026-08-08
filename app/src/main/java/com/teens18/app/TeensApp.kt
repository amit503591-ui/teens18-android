package com.teens18.app

import android.app.Application
import com.teens18.app.ads.AdManager
import com.teens18.app.theme.ThemeManager

class TeensApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        ThemeManager.applyTheme(this)
        AdManager.initialize(this)
    }
    companion object {
        lateinit var instance: TeensApp
            private set
    }
}