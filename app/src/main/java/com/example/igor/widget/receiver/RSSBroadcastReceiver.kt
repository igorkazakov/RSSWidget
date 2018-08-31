package com.example.igor.widget.receiver

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import com.example.igor.widget.service.UpdateService


class RSSBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("qqq", "onReceive RSSBroadcastReceiver")
        context.startService(Intent(context, UpdateService::class.java))
    }
}