package com.moengage.newsapp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moengage.newsapp.ui.newslist.MainViewModel
import java.lang.IllegalArgumentException

/**
 * A factory to provide the instance of our MainViewModel with desired constructor parameters
 * @param httpHandler to fetch data from internet
 * @param newsUrl the url from which data must be fetched
 */
class MainViewModelFactory(
    private val httpHandler: HttpHandler,
    private val newsUrl: String,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(httpHandler = httpHandler, newsUrl = newsUrl, application = application) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}