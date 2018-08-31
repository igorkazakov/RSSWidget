package com.example.igor.widget.service

import android.appwidget.AppWidgetManager
import android.content.*
import android.support.v4.app.JobIntentService
import android.util.Log
import android.view.View
import com.example.igor.rsswidjet.DataService.Repository
import com.example.igor.widget.DataService.models.Article
import com.example.igor.widget.screen.widget.AppWidget








class UpdateService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        onHandleIntent(intent)
    }

    private fun onHandleIntent(intent: Intent) {

        val receiver = getStopReceiver()

        try {

            while (true) {

                Log.e("qqq", "UpdateService mShouldStop = $mShouldStop")
                if (mShouldStop) {
                    unregisterReceiver(receiver)
                    stopSelf()
                    break
                }

                Log.e("qqq", "start loading UpdateService")

                Repository.instance.loadRss(intent.getStringExtra(RSS_URL),
                        object : Repository.ResponseCallback {

                    override fun success(response: Any?) {

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

    private fun getStopReceiver() : StopReceiver {
        val filter = IntentFilter(ACTION_STOP)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        val receiver = StopReceiver()
        registerReceiver(receiver, filter)
        return receiver
    }

    companion object {

        const val UPDATE = "UPDATE"
        const val RSS_URL = "RSS_URL"
        const val ACTION_STOP = "ACTION_STOP"
        private const val REFRESH_TIME = 18000L
        private const val SERVICE_JOB_ID = 50
        private var mShouldStop = false

        fun stopService(context: Context) {
            val sIntent = Intent()
            sIntent.action = ACTION_STOP
            context.sendBroadcast(sIntent)
        }

        fun enqueueWork(context: Context, work: Intent) {

            mShouldStop = false
            enqueueWork(context, UpdateService::class.java, SERVICE_JOB_ID, work)
        }

//        fun startService(context: Context) {
//
//            mShouldStop = false
//            val serviceIntent = Intent(context, UpdateService::class.java)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent.putExtra(UpdateService.RSS_URL,
//                        "https://lenta.ru/rss/articles"))
//
//            } else {
//                context.startService(serviceIntent.putExtra(UpdateService.RSS_URL,
//                        "https://lenta.ru/rss/articles"))
//            }
//        }
    }

    inner class StopReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            mShouldStop = true
        }
    }
}
