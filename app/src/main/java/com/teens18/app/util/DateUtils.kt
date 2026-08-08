package com.teens18.app.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    private val output = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    fun formatDate(iso: String): String = try {
        output.format(input.parse(iso.substring(0, 19)) ?: Date())
    } catch (e: Exception) { iso }
}