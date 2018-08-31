package com.example.igor.widget.application

import android.app.Application
import android.content.Context

class RssApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: RssApplication? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}