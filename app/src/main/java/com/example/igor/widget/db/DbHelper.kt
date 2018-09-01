package com.example.igor.widget.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.igor.widget.application.RssApplication
import com.example.igor.widget.db.dao.ArticleDao

class DbHelper(context: Context) :
        SQLiteOpenHelper(context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION) {

    private object HOLDER {
        val INSTANCE = DbHelper(RssApplication.applicationContext())
    }

    companion object {

        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "FeedReader.db"

        val instance: DbHelper by lazy { HOLDER.INSTANCE }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ArticleDao.createTable())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL(ArticleDao.removeTable())
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

}