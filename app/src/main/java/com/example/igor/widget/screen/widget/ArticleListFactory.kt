package com.example.igor.widget.screen.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.example.igor.widget.api.Repository
import com.example.igor.widget.api.models.Article
import com.example.igor.widget.R
import com.example.igor.widget.utils.PreferencesUtils


class ArticleListFactory(private var mContext: Context,
                         var intent: Intent) : RemoteViewsFactory {

    private lateinit var mData: List<Article>
    private var mWidgetId: Int = 0

    override fun onCreate() {

        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)
        mData = arrayListOf()
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

        Log.e("qqq", "getViewAt")
        val rView = RemoteViews(mContext.packageName, R.layout.item_article)
        val article = mData[position]
        rView.setTextViewText(R.id.articleTextView,
                article.content ?: article.description)
        AppWidget.hideLoadingView(mContext)

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

        Repository.instance.fetchArticle(articleId.toString(),
                object : Repository.ResponseCallback<Article> {

            override fun success(response: Article?) {
                Log.e("qqq", "setNewData success")
                response?.let {
                    mData = arrayListOf(it)
                }
            }

            override fun error(error: String) {
                Log.e("qqq", "onDataSetChanged error $error")
            }
        })
    }
}