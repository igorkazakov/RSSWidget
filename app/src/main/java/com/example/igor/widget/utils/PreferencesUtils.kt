package com.example.igor.widget.utils

import android.preference.PreferenceManager
import com.example.igor.widget.application.RssApplication

class PreferencesUtils() {

    private object HOLDER {
        val INSTANCE = PreferencesUtils()
    }

    companion object {

        val instance: PreferencesUtils by lazy { HOLDER.INSTANCE }

        private const val WIDGET_ID = "USER_LOGIN"
        private const val RSS_URL = "RSS_URL"
        private const val ARTICLE_ID = "ARTICLE_ID"
    }

    private var mSharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(RssApplication.applicationContext())


    fun saveWidgetId(value: Int) {
        setValue(WIDGET_ID, value)
    }

    fun removeWidgetId() {
        setValue(WIDGET_ID, -1)
    }

    fun getWidgetId() : Int {
        return getIntValue(WIDGET_ID, -1)
    }

    fun saveCurrentArticleId(value: Int) {
        setValue(ARTICLE_ID, value)
    }

    fun getCurrentArticleId() : Int {
        return getIntValue(ARTICLE_ID, -1)
    }

    fun saveRssUrl(value: String) {
        setStringValue(RSS_URL, value)
    }

    fun getRssUrl() : String {
        return getStringValue(RSS_URL, "")
    }

    private fun setValue(key: String, value: Int){
        val ed = mSharedPreferences.edit()
        ed.putInt(key, value).commit()
    }

    private fun setStringValue(key: String, value: String){
        val ed = mSharedPreferences.edit()
        ed.putString(key, value).commit()
    }

    private fun getIntValue(key: String, defValue: Int): Int {
        return mSharedPreferences.getInt(key, defValue)
    }

    private fun getStringValue(key: String, defValue: String): String {
        return mSharedPreferences.getString(key, defValue)
    }
}

