package com.teens18.app.ads

sealed class AdItem {
    abstract val id: String
    abstract val type: AdType
}
enum class AdType { BANNER, INTERSTITIAL, NATIVE, AFFILIATE }

data class HtmlBannerAd(
    override val id: String = "html_banner_${System.currentTimeMillis()}",
    override val type: AdType = AdType.BANNER,
    val networkName: String, val htmlContent: String
) : AdItem()

data class HtmlInterstitialAd(
    override val id: String = "html_interstitial_${System.currentTimeMillis()}",
    override val type: AdType = AdType.INTERSTITIAL,
    val networkName: String, val htmlContent: String
) : AdItem()

data class AffiliateAdItem(
    override val id: String,
    override val type: AdType = AdType.AFFILIATE,
    val imageUrl: String, val clickUrl: String, val label: String
) : AdItem()