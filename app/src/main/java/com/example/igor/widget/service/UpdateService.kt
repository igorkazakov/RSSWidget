package com.example.igor.widget.service

import android.app.IntentService
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import android.view.View
import com.example.igor.rsswidjet.DataService.Repository
import com.example.igor.widget.DataService.models.Article
import com.example.igor.widget.screen.widget.AppWidget


class UpdateService(name: String = "UpdateService") : IntentService(name) {

    override fun onHandleIntent(intent: Intent?) {

        try {

            while (true) {

                Log.e("qqq", "start loading UpdateService")
                AppWidget.loadingViewState(this@UpdateService, View.VISIBLE)

                Repository.instance.loadRss(intent?.getStringExtra(RSS_URL),
                        object : Repository.ResponseCallback {

                    override fun success(response: Any?) {

                        AppWidget.loadingViewState(this@UpdateService, View.GONE)

                        Log.e("qqq", "success UpdateService")

                        val intent = Intent(this@UpdateService,
                                AppWidget::class.java)
                        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                        val ids = AppWidgetManager.getInstance(this@UpdateService)
                                .getAppWidgetIds(ComponentName(this@UpdateService,
                                        AppWidget::class.java))

                        intent.putExtra(UPDATE, true)
                        if (ids != null && ids.isNotEmpty()) {
                            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

                            val article = response as? Article
                            article?.let {
                                intent.putExtra(AppWidget.ARTICLE, article)
                            }

                            this@UpdateService.sendBroadcast(intent)
                        }


                    }

                    override fun error(error: String) {
                        Log.e("qqq", "error UpdateService + $error")
                        AppWidget.loadingViewState(this@UpdateService, View.GONE)
                    }
                })

                Thread.sleep(REFRESH_TIME)
            }

        } catch (e: Exception) {
            e.toString()
            AppWidget.loadingViewState(this@UpdateService, View.GONE)
            Log.e("qqq", "exception UpdateService")
        }
    }

    companion object {

        const val UPDATE = "UPDATE"
        const val RSS_URL = "RSS_URL"
        private const val REFRESH_TIME = 18000L
    }
}
