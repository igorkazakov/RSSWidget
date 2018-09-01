package com.example.igor.widget.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.example.igor.widget.screen.widget.ArticleListFactory

class ArticleListService : RemoteViewsService() {

    override fun onGetViewFactory(p0: Intent): RemoteViewsFactory {
        return ArticleListFactory(applicationContext, p0)
    }
}