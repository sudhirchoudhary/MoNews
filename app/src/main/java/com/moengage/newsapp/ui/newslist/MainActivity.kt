package com.moengage.newsapp.ui.newslist

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.moengage.newsapp.MainViewModelFactory
import com.moengage.newsapp.MoApplication
import com.moengage.newsapp.R
import com.moengage.newsapp.adapter.NewsAdapter
import com.moengage.newsapp.adapter.OnItemClickListener
import com.moengage.newsapp.databinding.ActivityMainBinding
import com.moengage.newsapp.model.Article
import com.moengage.newsapp.ui.newsdetail.NewsDetailActivity
import com.moengage.newsapp.util.hide
import com.moengage.newsapp.util.makeToast
import com.moengage.newsapp.util.requestPermissionsIfNotGranted
import com.moengage.newsapp.util.show
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val notificationPermission = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
private const val NEWS_URL = "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"

class MainActivity : AppCompatActivity(), OnItemClickListener {
    private val adapter by lazy {
        NewsAdapter()
    }
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNecessaryObjects()
        initViews()
        checkForNotificationPermission()
        initUiStateCollector()
    }

    private fun checkForNotificationPermission(makeToast: Boolean = false) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            return

        requestPermissionsIfNotGranted(permissions = notificationPermission) {
            if(makeToast)
                makeToast(getString(R.string.you_ll_be_notified))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 101) {
            checkForNotificationPermission(true)
        }
    }

    private fun initViews() {
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = adapter
        adapter.setOnItemClickListener(this)

        binding.mcvRetry.setOnClickListener {
            viewModel.getNews()
        }

        binding.fabSort.setOnClickListener {
            viewModel.sortList()
        }
    }

    private fun initNecessaryObjects() {
        val httpHandler = (applicationContext as MoApplication).getHttpHandler()
        val factory = MainViewModelFactory(
            httpHandler,
            newsUrl = NEWS_URL,
            application
        )
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun initUiStateCollector() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.mainUiStateFlow.collect {
                    when(it) {
                        MainUiState.Loading -> {
                            binding.mcvRetry.isClickable = false
                            toggleErrorVisibility(false)
                            binding.progressCircular.show()
                            binding.fabSort.visibility = View.GONE
                        }
                        is MainUiState.Articles -> {
                            binding.mcvRetry.isClickable = true
                            binding.progressCircular.hide()
                            binding.rvMain.show()
                            toggleErrorVisibility(false)
                            adapter.addItems(it.articles)
                            binding.fabSort.visibility = View.VISIBLE

                            setDataInSortFab(it)
                        }
                        is MainUiState.Error -> {
                            binding.mcvRetry.isClickable = true
                            binding.rvMain.hide()
                            binding.progressCircular.hide()
                            binding.fabSort.visibility = View.GONE
                            setError(it.msg)
                        }
                    }
                }
            }
        }
    }

    private fun setDataInSortFab(it: MainUiState.Articles) {
        binding.fabSort.text = it.sortedText
    }

    private fun setError(msg: String) {
        binding.tvUserMessage.text = msg
        toggleErrorVisibility(true)
    }

    private fun toggleErrorVisibility(shouldVisible: Boolean = true) {
        if(shouldVisible) {
            binding.tvUserMessage.show()
            binding.mcvRetry.show()
        } else {
            binding.tvUserMessage.hide()
            binding.mcvRetry.hide()
        }
    }

    override fun onItemClick(article: Article) {
        startActivity(NewsDetailActivity.getIntent(this, article.url))
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }
}