package com.teens18.app.ads

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class AdWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : WebView(context, attrs) {

    interface AdInteractionListener {
        fun onAdClosed()
    }
    var listener: AdInteractionListener? = null

    init { setup() }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setup() {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        setBackgroundColor(Color.TRANSPARENT)
        isVerticalScrollBarEnabled = false
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    if (it.startsWith("http")) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        try { context.startActivity(intent) } catch (e: Exception) {}
                        return true
                    }
                }
                return false
            }
        }
        webChromeClient = WebChromeClient()
        addJavascriptInterface(object {
            @JavascriptInterface fun closeInterstitial() { listener?.onAdClosed() }
            @JavascriptInterface fun onAdClick(url: String) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try { context.startActivity(intent) } catch (e: Exception) {}
            }
        }, "Android")
    }

    fun loadHtmlAd(html: String, baseUrl: String = "https://teens18.info/") {
        loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null)
    }

    fun loadHtmlAdFromTemplate(template: String, adContent: String) {
        loadHtmlAd(template.replace("<!--__AD_CONTENT__-->", adContent))
    }

    fun loadNativeAd(title: String, body: String, iconUrl: String?, clickUrl: String, sponsoredBy: String) {
        val template = readRawFile("ad_native_template")
        val html = template
            .replace("__TITLE__", title)
            .replace("__BODY__", body)
            .replace("__ICON_URL__", iconUrl ?: "")
            .replace("__CLICK_URL__", clickUrl)
            .replace("__SPONSORED_BY__", sponsoredBy)
        loadHtmlAd(html)
    }

    private fun readRawFile(name: String): String {
        return try {
            val id = context.resources.getIdentifier(name, "raw", context.packageName)
            context.resources.openRawResource(id).bufferedReader().use { it.readText() }
        } catch (e: Exception) { "<html><body>Ad</body></html>" }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopLoading()
        loadUrl("about:blank")
    }
}