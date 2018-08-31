package com.example.igor.widget.screen.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.igor.rsswidjet.DataService.Repository
import com.example.igor.widget.DataService.models.Article
import com.example.igor.widget.R
import com.example.igor.widget.screen.settings.AppWidgetConfigureActivity
import com.example.igor.widget.service.ArticleListService
import com.example.igor.widget.service.UpdateService
import com.example.igor.widget.utils.PreferencesUtils

class AppWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val widgetId = PreferencesUtils.instance.getWidgetId()
        val articleId = PreferencesUtils.instance.getCurrentArticleId()

        when (intent.action) {
            PREVIOUS_CLICKED -> {

                Repository.instance.fetchPrevArticle(articleId.toString(),
                        object : Repository.ResponseCallback {

                            override fun success(response: Any?) {

                                val article = response as? Article
                                article?.let {
                                    saveArticleId(article.id)
                                    createNextOrPrevIntent(context, article)
                                }
                            }

                            override fun error(error: String) {}
                        })
            }

            NEXT_CLICKED -> {

                Repository.instance.fetchNextArticle(articleId.toString(),
                        object : Repository.ResponseCallback {

                            override fun success(response: Any?) {

                                val article = response as? Article
                                article?.let {
                                    saveArticleId(article.id)
                                    createNextOrPrevIntent(context, article)
                                }
                            }

                            override fun error(error: String) {}
                        })
            }

            DISABLE_CLICKED -> {

                Repository.instance.disableArticle(articleId.toString(),
                        object : Repository.ResponseCallback {

                            override fun success(response: Any?) {

                                val article = response as? Article
                                article?.let {
                                    saveArticleId(article.id)
                                    createNextOrPrevIntent(context, article)
                                }
                            }

                            override fun error(error: String) {}
                        })
            }

            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {

                val article: Article? = intent.extras.getParcelable(ARTICLE)
                if (article != null) {

                    if (articleId < 0) {

                        showArticle(context, widgetId, article, View.VISIBLE)

                    } else {
                        updateCurrentArticle(context, null, widgetId)
                    }
                }
            }

            MOVE_TO_ARTICLE -> {

                val article: Article? = intent.extras.getParcelable(ARTICLE)
                if (article != null) {
                    showArticle(context, widgetId, article, View.GONE)
                }
            }

            else -> {
            }
        }
    }

    override fun onUpdate(context: Context,
                          appWidgetManager: AppWidgetManager,
                          appWidgetIds: IntArray) {

//        if (PreferencesUtils.instance.getWidgetId() < 0) {
//            if (appWidgetIds.isNotEmpty()) {
//
//                Log.e("qqq", "service start")
//                val serviceIntent = Intent(context, UpdateService::class.java)
//                context.startService(serviceIntent.putExtra(UpdateService.RSS_URL,
//                        "https://lenta.ru/rss/articles"))
//                PreferencesUtils.instance.saveWidgetId(appWidgetIds[0])
//            }
//        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        Log.e("qqq", "service start")
        UpdateService.startService(context)

            //PreferencesUtils.instance.saveWidgetId(ids[0])
    }

    override fun onDisabled(context: Context) {
        Log.e("qqq", "onDisabled")
        PreferencesUtils.instance.removeWidgetId()
        UpdateService.stopService(context)
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

    private fun showArticle(context: Context, widgetId: Int, article: Article, showProgress: Int) {

        saveArticleId(article.id)
        updateAppWidget(context,
                null,
                widgetId,
                article,
                showProgress)
    }

    private fun saveArticleId(id: String?) {

        if (id != null) {
            PreferencesUtils.instance.saveCurrentArticleId(id.toInt())
        }
    }

    companion object {

        fun loadingViewState(context: Context, progress: Int) {

            val views = RemoteViews(context.packageName, R.layout.app_widget)
            views.setViewVisibility(R.id.progressLayout, progress)
            val widgetId = PreferencesUtils.instance.getWidgetId()
            AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views)
        }

        fun updateCurrentArticle(context: Context, appWidgetManager: AppWidgetManager?, widgetId: Int) {

            val articleId = PreferencesUtils.instance.getCurrentArticleId()

            Repository.instance.fetchArticle(articleId.toString(), object : Repository.ResponseCallback {

                override fun success(response: Any?) {

                    val article = response as? Article
                    if (article != null) {

                        updateAppWidget(context,
                                appWidgetManager,
                                widgetId,
                                article,
                                View.GONE)
                    }
                }

                override fun error(error: String) {}
            })
        }

        internal fun updateAppWidget(context: Context,
                                     appWidgetManager: AppWidgetManager?,
                                     appWidgetId: Int,
                                     article: Article,
                                     showProgress: Int) {

            val views = RemoteViews(context.packageName, R.layout.app_widget)

            setRemoteAdapter(context, views, article)

            views.setTextViewText(R.id.titleTextView, article.title)
            views.setViewVisibility(R.id.progressLayout, showProgress)

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
                val adapterIntent = Intent(context, ArticleListService::class.java)
                views.setRemoteAdapter(R.id.bodyTextListView, adapterIntent)

                val widgetId = PreferencesUtils.instance.getWidgetId()
                AppWidgetManager.getInstance(context)
                        .notifyAppWidgetViewDataChanged(widgetId, R.id.bodyTextListView)
            }
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

