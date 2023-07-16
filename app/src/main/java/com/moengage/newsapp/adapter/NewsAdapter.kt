package com.moengage.newsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moengage.newsapp.databinding.NewsItemBinding
import com.moengage.newsapp.model.Article
import com.moengage.newsapp.util.convertToHumanReadableDate
import com.squareup.picasso.Picasso
import java.util.function.BiPredicate

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    private val _newsList = mutableListOf<Article>()
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.ViewHolder {
        return ViewHolder(
            itemBinding = NewsItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClickListener = onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: NewsAdapter.ViewHolder, position: Int) {
        (holder).loadDataIntoUi(_newsList[position])
    }

    override fun getItemCount() = _newsList.size

    fun addItems(newsList: List<Article>, clear: Boolean = true) {
        if(clear) {
            val size = _newsList.size
            _newsList.clear()
            notifyItemRangeRemoved(0, size)
        }
        val size = _newsList.size
        _newsList.addAll(newsList)
        notifyItemRangeInserted(size, newsList.size)
    }

    inner class ViewHolder(
        private val itemBinding: NewsItemBinding,
        private val onItemClickListener: OnItemClickListener?
    ) : RecyclerView.ViewHolder(itemBinding.root){
        fun loadDataIntoUi(article: Article) {
            itemBinding.tvTitle.text = article.title
            itemBinding.tvDescription.text = article.description
            itemBinding.textView3.text = article.publishedAt.convertToHumanReadableDate()

            Picasso.get().load(article.urlToImage).into(itemBinding.imageView)

            itemBinding.root.setOnClickListener {
                onItemClickListener?.onItemClick(article)
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}

interface OnItemClickListener{
    fun onItemClick(article: Article)
}
