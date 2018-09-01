package com.example.igor.widget.utils

object NetworkUtils {

    fun isValidUrl(url: String?): Boolean {
        if (url == null) {
            return false
        }

        val urlPattern = Regex("^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\." +
                "([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*")
        return url.matches(urlPattern)
    }
}
