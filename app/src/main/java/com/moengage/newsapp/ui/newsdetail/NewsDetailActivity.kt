package com.moengage.newsapp.ui.newsdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.moengage.newsapp.databinding.ActivityNewsDetailBinding
import com.moengage.newsapp.util.makeToast

class NewsDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsDetailBinding
    private var url: String? = null

    companion object {
        private const val EXTRA_URL = "url"
        fun getIntent(context: Context, url: String): Intent {
            val intent = Intent(context, NewsDetailActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDataFromBundle()

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                binding.webView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }

        url?.let {
            binding.webView.loadUrl(it)
        } ?: {
            makeToast("Something went wrong!")
            finish()
        }

        // this will enable the javascript settings
        binding.webView.settings.javaScriptEnabled = true

        // if you want to enable zoom feature
        binding.webView.settings.setSupportZoom(true)
    }

    private fun getDataFromBundle() {
        intent.extras?.apply {
            if(containsKey(EXTRA_URL))
                url = getString(EXTRA_URL)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.clearCache(true)
        binding.webView.destroy()
    }
}