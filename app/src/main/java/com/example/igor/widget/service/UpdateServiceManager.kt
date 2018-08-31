package com.example.igor.widget.service

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.JobIntentService
import android.util.Log
import android.view.View
import com.example.igor.rsswidjet.DataService.Repository
import com.example.igor.widget.DataService.models.Article
import com.example.igor.widget.screen.widget.AppWidget

class UpdateServiceManager {

    companion object {

        const val UPDATE = "UPDATE"
        const val RSS_URL = "RSS_URL"
        const val ACTION_STOP = "ACTION_STOP"
        const val REFRESH_TIME = 18000L
        const val SERVICE_JOB_ID = 50
        var mShouldStop = false

        fun stopService() {
            mShouldStop = true
        }

        fun startService(context: Context) {

            mShouldStop = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                JobIntentService.enqueueWork(context,
                        UpdateService::class.java, SERVICE_JOB_ID,
                        getServiceIntent(context, UpdateService::class.java))

            } else {
                context.startService(getServiceIntent(context, UpdateServiceCompat::class.java))
            }
        }

        fun doUpdateWork(intent: Intent, service: Service) {

            val context = service.applicationContext

            try {

                while (true) {

                    Log.e("qqq", "UpdateService mShouldStop = $mShouldStop")
                    if (mShouldStop) {
                        service.stopSelf()
                        break
                    }

                    Log.e("qqq", "start loading UpdateService")

                    Repository.instance.loadRss(intent.getStringExtra(RSS_URL),
                            object : Repository.ResponseCallback {

                                override fun success(response: Any?) {

                                    val updateIntent = Intent(context,
                                            AppWidget::class.java)
                                    updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                    val ids = AppWidgetManager.getInstance(context)
                                            .getAppWidgetIds(ComponentName(context,
                                                    AppWidget::class.java))

                                    updateIntent.putExtra(UPDATE, true)
                                    if (ids != null && ids.isNotEmpty()) {
                                        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

                                        val article = response as? Article
                                        article?.let {
                                            updateIntent.putExtra(AppWidget.ARTICLE, article)
                                        }

                                        context.sendBroadcast(updateIntent)
                                    }
                                }

                                override fun error(error: String) {
                                    Log.e("qqq", "error UpdateService + $error")
                                }
                            })

                    Thread.sleep(REFRESH_TIME)
                }

            } catch (e: Exception) {
                e.toString()
                AppWidget.loadingViewState(context, View.GONE)
                Log.e("qqq", "exception UpdateService")
            }
        }

        private fun getServiceIntent(context: Context, serviceClass: Class<*>) : Intent {
            val serviceIntent = Intent(context, serviceClass)
            serviceIntent.putExtra(RSS_URL, "https://lenta.ru/rss/articles")

            return serviceIntent
        }
    }
}