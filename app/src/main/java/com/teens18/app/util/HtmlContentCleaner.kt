package com.teens18.app.util

import org.jsoup.Jsoup

object HtmlContentCleaner {
    fun clean(html: String): String {
        val doc = Jsoup.parse(html)
        doc.select("script").remove()
        doc.select("style").remove()
        doc.select("iframe").remove()
        return doc.html()
    }
}