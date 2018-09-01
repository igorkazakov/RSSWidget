package com.example.igor.widget.screen.settings

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.example.igor.widget.api.models.Article
import com.example.igor.widget.R

class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private lateinit var mTitle: TextView
    private lateinit var mEnableButton: ImageButton

    fun bind(article: Article, listener: ArticleHolderListener) {

        mTitle = itemView.findViewById(R.id.titleText)
        mEnableButton = itemView.findViewById(R.id.enableButton)

        mTitle.text = article.title
        mEnableButton.setOnClickListener {

            article.id?.let {
                listener.onEnableClick(it)
            }
        }
    }

    interface ArticleHolderListener {
        fun onEnableClick(articleId: String)
    }
}