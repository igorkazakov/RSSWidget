package com.example.igor.widget.service

import android.app.IntentService
import android.content.Intent

class UpdateServiceCompat(name: String = "UpdateServiceCompat") : IntentService(name) {

    override fun onHandleIntent(intent: Intent) {
        UpdateServiceManager.doUpdateWork(this)
    }
}