package com.example.igor.widget.receiver

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import android.util.Log
import com.example.igor.widget.service.UpdateService


class RSSBroadcastReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        val comp = ComponentName(context.packageName,
                UpdateService::class.java.name)
        // Start the service, keeping the device awake while it is launching.
        // startWakefulService(context, (intent.setComponent(comp)));

        //setResultCode(Activity.RESULT_OK);
        Log.e("qqq", "RSSBroadcastReceiver wake up!")
        intent.putExtra(UpdateService.RSS_URL, "https://lenta.ru/rss/articles")
        UpdateService.enqueueWork(context, intent.setComponent(comp))
    }
}