package com.example.igor.widget.service

import android.content.Intent
import android.support.v4.app.JobIntentService


class UpdateService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        onHandleIntent()
    }

    private fun onHandleIntent() {
        UpdateServiceManager.doUpdateWork(this)
    }
}
