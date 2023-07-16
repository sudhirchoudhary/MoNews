package com.moengage.newsapp.ui.newslist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moengage.newsapp.HttpHandler
import com.moengage.newsapp.R
import com.moengage.newsapp.model.Article
import com.moengage.newsapp.model.Source
import com.moengage.newsapp.util.isNetworkConnected
import com.moengage.newsapp.util.log
import com.moengage.newsapp.util.sortItemsOldestToRecent
import com.moengage.newsapp.util.sortItemsRecentToOldest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class MainViewModel(
    private val application: Application,
    private val newsUrl: String,
    private val httpHandler: HttpHandler,
) : AndroidViewModel(application) {
    private val _newsListFlow = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val mainUiStateFlow = _newsListFlow.asStateFlow()

    private var apiCallInProgress = false
    private var job: Job? = null

    init {
        getNews()
    }

    fun getNews() {
        if (apiCallInProgress) {
            return
        }

        if (!application.isNetworkConnected()) {
            setNetworkError(application.getString(R.string.no_internet))
            return
        }
        apiCallInProgress = true
        _newsListFlow.value = MainUiState.Loading
        //launch the http request with IO dispatcher
        job = viewModelScope.launch(Dispatchers.IO) {
            val res = httpHandler.makeServiceCall(newsUrl)
            res?.let {
                withContext(Dispatchers.Default) {
                    val list = mapToArticleList(it)
                    apiCallInProgress = false
                    if (list.isNotEmpty()) {
                        _newsListFlow.value = MainUiState.Articles(
                            articles = list
                        )
                    } else {
                        _newsListFlow.value = MainUiState.Error(
                            msg = "Something went wrong!"
                        )
                    }
                }
            } ?: run {
                apiCallInProgress = false
                _newsListFlow.value = MainUiState.Error(
                    msg = "Something went wrong!"
                )
            }
        }

        viewModelScope.launch {
            delay(10_000L)
            if (job?.isActive == true) {
                log("cancelling job")
                job?.cancel()
            } else return@launch

            apiCallInProgress = false
            _newsListFlow.value = MainUiState.Error(
                msg = "Something went wrong!"
            )
        }
    }

    fun sortList() {
        if (_newsListFlow.value !is MainUiState.Articles)
            return
        val article = (_newsListFlow.value as MainUiState.Articles)

        _newsListFlow.value = MainUiState.Loading

        viewModelScope.launch(Dispatchers.Default) {
            val sortedText: String
            val sortedList = if (article.sortedText.equals("oldest", true)) {
                sortedText = "Recent"
                article.articles.sortItemsRecentToOldest()
            } else {
                sortedText = "Oldest"
                article.articles.sortItemsOldestToRecent()
            }
            _newsListFlow.value = MainUiState.Articles(
                sortedList,
                sortedText
            )
        }
    }

    /**
     * parse the JSON String to the Article's List
     */
    private fun mapToArticleList(res: String): List<Article> {
        val articleList = mutableListOf<Article>()
        try {
            val response = JSONObject(res)
            val articles = response.getJSONArray("articles")
            for (x in 0 until articles.length()) {
                val jsonArticle = articles.getJSONObject(x)
                val author = jsonArticle.getString("author")
                val title = jsonArticle.getString("title")
                val description = jsonArticle.getString("description")
                val url = jsonArticle.getString("url")
                val urlToImage = jsonArticle.getString("urlToImage")
                val publishedAt = jsonArticle.getString("publishedAt")
                val content = jsonArticle.getString("content")
                val jsonSource = jsonArticle.getJSONObject("source")
                val id = jsonSource.getString("id")
                val name = jsonSource.getString("name")

                val source = Source(
                    id = id,
                    name = name
                )

                val article = Article(
                    author = author,
                    description = description,
                    title = title,
                    url = url,
                    urlToImage = urlToImage,
                    publishedAt = publishedAt,
                    content = content,
                    source = source
                )
                articleList.add(article)
            }
        } catch (ignore: JSONException) {
        }
        return articleList
    }

    private fun setNetworkError(msg: String) {
        _newsListFlow.value = MainUiState.Error(
            msg = msg
        )
    }
}

/**
 * A closed hierarchy to represent the UI State of our main screen.
 */
sealed interface MainUiState {
    object Loading : MainUiState
    data class Articles(
        val articles: List<Article> = emptyList(),
        val sortedText: String = "Recent"
    ) : MainUiState

    data class Error(
        val msg: String = ""
    ) : MainUiState
}