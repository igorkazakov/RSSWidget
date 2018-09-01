package com.example.igor.widget.screen.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.igor.widget.api.Repository
import com.example.igor.widget.api.models.Article
import com.example.igor.widget.R
import com.example.igor.widget.screen.settings.AppWidgetConfigureActivity
import com.example.igor.widget.service.ArticleListService
import com.example.igor.widget.service.UpdateServiceManager
import com.example.igor.widget.utils.PreferencesUtils

class AppWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val widgetId = PreferencesUtils.instance.getWidgetId()
        val articleId = PreferencesUtils.instance.getCurrentArticleId()

        val navigationButtonsResponse = object : Repository.ResponseCallback<Article> {

            override fun success(response: Article?) {

                response?.let {
                    saveArticleId(it.id)
                    createNextOrPrevIntent(context, it)
                }
            }

            override fun error(error: String) {}
        }

        when (intent.action) {
            PREVIOUS_CLICKED -> {

                Repository.instance.fetchPrevArticle(articleId.toString(),
                        navigationButtonsResponse)
            }

            NEXT_CLICKED -> {

                Repository.instance.fetchNextArticle(articleId.toString(),
                        navigationButtonsResponse)
            }

            DISABLE_CLICKED -> {

                Repository.instance.disableArticle(articleId.toString(),
                        navigationButtonsResponse)
            }

            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {

                val article: Article? = intent.extras.getParcelable(ARTICLE)
                if (article != null) {

                    if (articleId < 0) {
                        showArticle(context, widgetId, article)

                    } else {
                        updateCurrentArticle(context, null, widgetId)
                    }
                }
            }

            MOVE_TO_ARTICLE -> {

                val article: Article? = intent.extras.getParcelable(ARTICLE)
                if (article != null) {
                    showArticle(context, widgetId, article)
                }
            }

            else -> {}
        }
    }

    override fun onDisabled(context: Context) {

        PreferencesUtils.instance.removeWidgetId()
        UpdateServiceManager.stopService()
        super.onDisabled(context)
    }

    fun createNextOrPrevIntent(context: Context,
                               article: Article) {

        val intent = Intent(context,
                AppWidget::class.java)
        intent.action = MOVE_TO_ARTICLE
        intent.putExtra(ARTICLE, article)
        context.sendBroadcast(intent)
    }

    private fun showArticle(context: Context, widgetId: Int, article: Article) {

        saveArticleId(article.id)
        updateAppWidget(context,
                null,
                widgetId,
                article)
    }

    private fun saveArticleId(id: String?) {

        if (id != null) {
            PreferencesUtils.instance.saveCurrentArticleId(id.toInt())
        }
    }

    companion object {

        fun showLoadingView(context: Context) {
            loadingViewState(context, View.VISIBLE)
        }

        fun hideLoadingView(context: Context) {
            loadingViewState(context, View.GONE)
        }

        fun updateCurrentArticle(context: Context, appWidgetManager: AppWidgetManager?, widgetId: Int) {

            val articleId = PreferencesUtils.instance.getCurrentArticleId()

            Repository.instance.fetchArticle(articleId.toString(), object :
                    Repository.ResponseCallback<Article> {

                override fun success(response: Article?) {

                    response?.let {
                        updateAppWidget(context,
                                appWidgetManager,
                                widgetId,
                                it)
                    }
                }

                override fun error(error: String) {}
            })
        }

        fun updateAppWidget(context: Context,
                                     appWidgetManager: AppWidgetManager?,
                                     appWidgetId: Int,
                                     article: Article) {

            val views = RemoteViews(context.packageName, R.layout.app_widget)
            setRemoteAdapter(context, views, article)
            views.setTextViewText(R.id.titleTextView, article.title)

            views.setOnClickPendingIntent(R.id.leftButton,
                    getPendingSelfIntent(context,
                            PREVIOUS_CLICKED,
                            appWidgetId))

            views.setOnClickPendingIntent(R.id.rightButton,
                    getPendingSelfIntent(context,
                            NEXT_CLICKED,
                            appWidgetId))

            views.setOnClickPendingIntent(R.id.disableButton,
                    getPendingSelfIntent(context,
                            DISABLE_CLICKED,
                            appWidgetId))

            views.setOnClickPendingIntent(R.id.settingsButton,
                    getPendingActivityIntent(context,
                            SETTINGS_CLICKED))

            if (appWidgetManager != null) {
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } else {
                AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views)
            }
        }

        private fun setRemoteAdapter(context: Context,
                                     views: RemoteViews,
                                     article: Article?) {

            article?.let {
                val widgetId = PreferencesUtils.instance.getWidgetId()
                val adapterIntent = Intent(context, ArticleListService::class.java)
                adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                views.setRemoteAdapter(R.id.bodyTextListView, adapterIntent)

                AppWidgetManager.getInstance(context)
                        .notifyAppWidgetViewDataChanged(widgetId, R.id.bodyTextListView)
            }
        }

        private fun loadingViewState(context: Context, progress: Int) {

            val views = RemoteViews(context.packageName, R.layout.app_widget)
            views.setViewVisibility(R.id.progressLayout, progress)
            val widgetId = PreferencesUtils.instance.getWidgetId()
            AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views)
        }

        private fun getPendingSelfIntent(context: Context,
                                         action: String,
                                         appWidgetId: Int): PendingIntent {

            val intentUpdate = Intent(context, AppWidget::class.java)
            intentUpdate.action = action
            val idArray = intArrayOf(appWidgetId)
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray)

            return PendingIntent.getBroadcast(
                    context, 0, intentUpdate, 0)
        }

        private fun getPendingActivityIntent(context: Context, action: String): PendingIntent {
            val intent = Intent(context, AppWidgetConfigureActivity::class.java)
            intent.action = action
            intent.putExtra(SETTINGS_CLICKED, true)
            return PendingIntent.getActivity(context, 0, intent, 0)
        }

        const val SETTINGS_CLICKED = "SETTINGS_CLICKED"
        const val ARTICLE = "ARTICLE"
        const val PREVIOUS_CLICKED = "PREVIOUS_CLICKED"
        const val NEXT_CLICKED = "NEXT_CLICKED"
        const val DISABLE_CLICKED = "DISABLE_CLICKED"
        const val MOVE_TO_ARTICLE = "MOVE_TO_ARTICLE"
    }
}

