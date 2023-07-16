package com.moengage.newsapp.util

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.moengage.newsapp.model.Article
import java.text.SimpleDateFormat
import java.util.Locale

fun log(message: String, tag: String = "RequestX") = Log.d(tag, message)

fun Context.makeToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun String.convertToHumanReadableDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val outputFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.US)

        val date = inputFormat.parse(this)
        date?.let {
            outputFormat.format(date)
        } ?: this
    } catch (e: Exception) {
        this
    }
}

fun View.show(): View {
    visibility = View.VISIBLE
    return this
}

fun View.hide(): View {
    visibility = View.GONE
    return this
}

fun List<Article>.sortItemsOldestToRecent(): List<Article> {
    return sortedBy { item ->
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val date = inputFormat.parse(item.publishedAt)
        date
    }
}

fun List<Article>.sortItemsRecentToOldest(): List<Article> {
    return sortedByDescending { item ->
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val date = inputFormat.parse(item.publishedAt)
        date
    }
}