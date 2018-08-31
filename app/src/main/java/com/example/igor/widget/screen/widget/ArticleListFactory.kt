package com.example.igor.widget.screen.widget

import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.example.igor.rsswidjet.DataService.Repository
import com.example.igor.widget.DataService.models.Article
import com.example.igor.widget.R
import com.example.igor.widget.utils.PreferencesUtils


class ArticleListFactory(private var mContext: Context) : RemoteViewsFactory {

    private var mData: List<Article> = mutableListOf()

    override fun onCreate() {
        setNewData()
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewAt(position: Int): RemoteViews {
        val rView = RemoteViews(mContext.packageName, R.layout.item_article)
        val article = mData[position]
        rView.setTextViewText(R.id.articleTextView, article.content ?: article.description)
        return rView
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun onDataSetChanged() {
        setNewData()
    }

    override fun onDestroy() {}

    private fun setNewData() {

        val articleId = PreferencesUtils.instance.getCurrentArticleId()

        Repository.instance.fetchArticle(articleId.toString(), object : Repository.ResponseCallback {

            override fun success(response: Any?) {

                val article = response as? Article
                if (article != null) {
                    mData = arrayListOf(article)
                }
            }

            override fun error(error: String) {
                Log.e("qqq", "onDataSetChanged error $error")
            }
        })
    }
}