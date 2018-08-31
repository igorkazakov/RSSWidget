package com.example.igor.widget.db

import android.database.sqlite.SQLiteDatabase

class DbManager {

    private var mOpenCounter: Int = 0
    private var mDatabase: SQLiteDatabase? = null

    init {
        mDbHelper = DbHelper.instance
    }

    @Synchronized
    fun openDatabase(): SQLiteDatabase? {
        mOpenCounter++
        if (mOpenCounter == 1) {

            mDatabase = mDbHelper?.writableDatabase
        }
        return mDatabase
    }

    @Synchronized
    fun closeDatabase() {
        mOpenCounter--
        if (mOpenCounter == 0) {
            mDatabase?.close()
        }
    }

    private object HOLDER {
        val INSTANCE = DbManager()
    }

    companion object {

        private var mDbHelper: DbHelper? = null
        val instance: DbManager by lazy { HOLDER.INSTANCE }
    }
}