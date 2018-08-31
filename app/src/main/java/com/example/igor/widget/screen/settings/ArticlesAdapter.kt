package com.example.igor.widget.screen.settings

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.igor.widget.DataService.models.Article
import com.example.igor.widget.R

class ArticlesAdapter(private val mList: List<Article>,
                      private val mListener: ArticlesAdapterListener) :
        RecyclerView.Adapter<ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {

        return ArticleViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_disable_article, parent, false))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(mList[position], object : ArticleViewHolder.ArticleHolderListener {

            override fun onEnableClick(articleId: String) {
                mListener.enableArticle(articleId)
            }
        })
    }

    interface ArticlesAdapterListener {
        fun enableArticle(articleId: String)
    }
}