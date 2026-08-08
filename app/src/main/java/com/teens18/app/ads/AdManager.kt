package com.teens18.app.ads

import android.content.Context
import android.view.ViewGroup
import com.teens18.app.util.NetworkUtil

object AdManager {
    private var config: AdsConfig? = null

    fun initialize(context: Context) { config = AdConfigLoader.load(context) }

    fun isEnabled(context: Context): Boolean {
        if (config == null) config = AdConfigLoader.load(context)
        val cfg = config ?: return false
        if (!cfg.ads_enabled) return false
        if (!NetworkUtil.isOnline(context)) return false
        return true
    }

    fun createBannerAd(context: Context, parent: ViewGroup): AdWebView? {
        if (!isEnabled(context)) return null
        val cfg = config ?: return null
        val ad = pickBanner(cfg) ?: return null
        val webView = AdWebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        parent.removeAllViews()
        parent.addView(webView)
        webView.loadHtmlAdFromTemplate(readTemplate(context, "ad_banner_template"), ad.htmlContent)
        return webView
    }

    private fun pickBanner(cfg: AdsConfig): HtmlBannerAd? {
        if (cfg.networks.google_adsense.enabled) {
            val slot = cfg.networks.google_adsense.slots["banner"]
            if (!slot.isNullOrEmpty()) {
                return HtmlBannerAd("AdSense", generateAdSense(cfg.networks.google_adsense.publisher_id, slot))
            }
        }
        if (cfg.networks.custom_html.enabled) {
            cfg.networks.custom_html.networks
                .firstOrNull { it.enabled && it.types.contains("banner") }
                ?.let { return HtmlBannerAd(it.name, it.script) }
        }
        return null
    }

    fun showInterstitialAd(context: Context, onClose: () -> Unit) {
        if (!isEnabled(context)) { onClose(); return }
        val cfg = config ?: run { onClose(); return }
        val ad = cfg.networks.google_adsense.slots["interstitial"]?.let {
            HtmlInterstitialAd("AdSense", generateAdSense(cfg.networks.google_adsense.publisher_id, it, "auto"))
        } ?: cfg.networks.custom_html.networks
            .firstOrNull { it.enabled && it.types.contains("interstitial") }
            ?.let { HtmlInterstitialAd(it.name, it.script) } ?: run { onClose(); return }

        val dialog = android.app.Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val webView = AdWebView(context)
        webView.listener = object : AdWebView.AdInteractionListener { override fun onAdClosed() { dialog.dismiss(); onClose() } }
        dialog.setContentView(webView)
        dialog.setOnDismissListener { onClose() }
        dialog.show()
        webView.loadHtmlAdFromTemplate(readTemplate(context, "ad_interstitial"), ad.htmlContent)
    }

    fun createNativeAd(context: Context, parent: ViewGroup): AdWebView? {
        if (!isEnabled(context)) return null
        val cfg = config ?: return null
        val network = cfg.networks.custom_html.networks
            .firstOrNull { it.enabled && it.types.contains("native") } ?: return null
        val webView = AdWebView(context)
        parent.removeAllViews()
        parent.addView(webView)
        webView.loadNativeAd("Recommended", "Tap to discover more",
            null, "javascript:void(0)", "Sponsored by ${network.name}")
        return webView
    }

    private fun generateAdSense(publisherId: String, slot: String, format: String = "auto"): String = """
        <ins class="adsbygoogle" style="display:block"
             data-ad-client="$publisherId" data-ad-slot="$slot"
             data-ad-format="$format" data-full-width-responsive="true"></ins>
        <script>(adsbygoogle = window.adsbygoogle || []).push({});</script>
    """.trimIndent()

    private fun readTemplate(context: Context, name: String): String {
        return try {
            val id = context.resources.getIdentifier(name, "raw", context.packageName)
            context.resources.openRawResource(id).bufferedReader().use { it.readText() }
        } catch (e: Exception) { "<html><body></body></html>" }
    }

    fun getBannerFrequency(context: Context): Int {
        if (config == null) config = AdConfigLoader.load(context)
        return config?.ads_frequency?.banner_in_list ?: 5
    }
    fun getInterstitialFrequency(context: Context): Int {
        if (config == null) config = AdConfigLoader.load(context)
        return config?.ads_frequency?.interstitial_after_articles ?: 3
    }
}