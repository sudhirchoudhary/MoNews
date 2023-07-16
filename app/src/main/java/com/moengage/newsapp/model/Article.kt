package com.moengage.newsapp.model

data class Article(
    val author: String = "",
    val title: String = "",
    val description: String = "",
    val url: String = "",
    val urlToImage: String = "",
    val publishedAt: String = "",
    val content: String = "",
    val source: Source = Source()
)

data class Source(
    val id: String = "",
    val name: String = ""
)