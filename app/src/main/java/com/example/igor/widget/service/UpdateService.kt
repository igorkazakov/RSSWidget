package com.example.igor.widget.service

import android.content.Intent
import android.support.v4.app.JobIntentService


class UpdateService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        onHandleIntent(intent)
    }

    private fun onHandleIntent(intent: Intent) {
        UpdateServiceManager.doUpdateWork(intent, this)
    }
}
