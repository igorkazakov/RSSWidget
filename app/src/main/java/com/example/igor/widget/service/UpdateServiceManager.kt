package com.example.igor.widget.service

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.*
import android.os.Build
import android.support.v4.app.JobIntentService
import android.util.Log
import com.example.igor.widget.api.Repository
import com.example.igor.widget.api.models.Article
import com.example.igor.widget.screen.widget.AppWidget
import com.example.igor.widget.utils.PreferencesUtils

class UpdateServiceManager {

    companion object {

        const val UPDATE = "UPDATE"
        private const val REFRESH_TIME = 60000L
        private const val SERVICE_JOB_ID = 50
        private var mIsStopped = true

        fun stopService() {
            mIsStopped = true
        }

        fun startService(context: Context) {

            if (mIsStopped) {
                mIsStopped = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    JobIntentService.enqueueWork(context,
                            UpdateService::class.java, SERVICE_JOB_ID,
                            getServiceIntent(context, UpdateService::class.java))

                } else {
                    context.startService(getServiceIntent(context, UpdateServiceCompat::class.java))
                }
            }
        }

        fun updateDataWithNewRssUrl(context: Context) {
            backgroundWork(context, true)
        }

        fun forceUpdateData(context: Context) {

            AppWidget.showLoadingView(context)
            backgroundWork(context, false)
        }

        fun doUpdateWork(service: Service) {

            val context = service.applicationContext

            try {

                while (true) {

                    if (mIsStopped) {
                        service.stopSelf()
                        break
                    }

                    backgroundWork(context, false)
                    Thread.sleep(REFRESH_TIME)
                }

            } catch (e: Exception) {
                e.toString()
                Log.e("qqq", "exception UpdateService")
            }
        }

        private fun backgroundWork(context: Context, shouldUpdateUrl: Boolean) {

            val url = PreferencesUtils.instance.getRssUrl()

            Repository.instance.loadRss(url,
                    shouldUpdateUrl,
                    object : Repository.ResponseCallback<Article> {

                        override fun success(response: Article?) {

                            response?.let {
                                val widgetId = PreferencesUtils.instance.getWidgetId()
                                val updateIntent = Intent(context,
                                        AppWidget::class.java)
                                updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                updateIntent.putExtra(UPDATE, true)
                                updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                                        intArrayOf(widgetId))
                                updateIntent.putExtra(AppWidget.ARTICLE, response)
                                PreferencesUtils.instance.saveCurrentArticleId(response.id?.toInt() ?: -1)

                                context.sendBroadcast(updateIntent)
                            }
                        }

                        override fun error(error: String) {
                            AppWidget.showLoadingView(context)
                            Log.e("qqq", "error UpdateService + $error")
                        }
                    })
        }

        private fun getServiceIntent(context: Context,
                                     serviceClass: Class<*>): Intent {
            return Intent(context, serviceClass)
        }
    }
}