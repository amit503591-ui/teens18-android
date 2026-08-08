package com.teens18.app.ads

import android.content.Context
import com.google.gson.Gson

data class AdsConfig(
    val ads_enabled: Boolean = true,
    val show_ads_when_offline: Boolean = false,
    val ads_frequency: AdsFrequency = AdsFrequency(),
    val networks: Networks = Networks()
)
data class AdsFrequency(
    val banner_in_list: Int = 5,
    val interstitial_after_articles: Int = 3
)
data class Networks(
    val google_adsense: GoogleAdSense = GoogleAdSense(),
    val custom_html: CustomHtml = CustomHtml()
)
data class GoogleAdSense(
    val enabled: Boolean = false,
    val publisher_id: String = "",
    val slots: Map<String, String> = emptyMap()
)
data class CustomHtml(
    val enabled: Boolean = false,
    val networks: List<CustomNetwork> = emptyList()
)
data class CustomNetwork(
    val name: String, val enabled: Boolean = false,
    val script: String, val types: List<String> = emptyList()
)

object AdConfigLoader {
    private var cached: AdsConfig? = null
    fun load(context: Context): AdsConfig {
        if (cached != null) return cached!!
        return try {
            val id = context.resources.getIdentifier("ads_config", "raw", context.packageName)
            val json = context.resources.openRawResource(id).bufferedReader().use { it.readText() }
            Gson().fromJson(json, AdsConfig::class.java).also { cached = it }
        } catch (e: Exception) { AdsConfig().also { cached = it } }
    }
}