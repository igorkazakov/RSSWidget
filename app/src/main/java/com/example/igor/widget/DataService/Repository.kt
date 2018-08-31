package com.example.igor.rsswidjet.DataService

import android.os.Handler
import android.os.Looper
import com.example.igor.widget.DataService.models.Article
import com.example.igor.widget.db.dao.ArticleDao
import com.example.igor.widget.utils.RSSParser
import java.net.URL





class Repository {

    private object HOLDER {
        val INSTANCE = Repository()
    }

    companion object {
        val instance: Repository by lazy { HOLDER.INSTANCE }
    }

    interface ResponseCallback {
        fun success(response: Any?)
        fun error(error: String)
    }

    fun loadRss(url: String?, responseHandler: ResponseCallback?) {

        if (url != null) {

            var article: Article? = null
            val articleList: List<Article>

            try {

                val result = URL(url).readText()
                val array = RSSParser.parseXML(result)

                ArticleDao.insertOrUpdate(array)
                articleList = ArticleDao.fetchArticles()

            } catch (e: Exception) {
                responseHandler?.error(e.message ?: "Something went wrong")
                return
            }

            if (articleList.isNotEmpty()) {
                article = articleList.first()
            }

            responseHandler?.success(article)

        } else {
            responseHandler?.error("Url is empty!")
        }
    }

    fun fetchArticles(responseHandler: ResponseCallback?) {

        try {

            Thread(Runnable {
                responseHandler?.success(ArticleDao.fetchArticles())
            }).start()

        } catch (e: Exception) {
            responseHandler?.error(e.message ?: "Something went wrong")
        }
    }

    fun fetchDisableArticles(responseHandler: ResponseCallback?) {

        try {

            val uiHandler = Handler(Looper.getMainLooper())
            Thread(Runnable {
                val articles = ArticleDao.fetchDisableArticles()

                uiHandler.post {
                    responseHandler?.success(articles)
                }

            }).start()

        } catch (e: Exception) {
            responseHandler?.error(e.message ?: "Something went wrong")
        }
    }

    fun fetchArticle(id: String, responseHandler: ResponseCallback?) {

        try {

            Thread(Runnable {
                responseHandler?.success(ArticleDao.getArticleById(id))
            }).start()

        } catch (e: Exception) {
            responseHandler?.error(e.message ?: "Something went wrong")
        }
    }

    fun fetchNextArticle(id: String, responseHandler: ResponseCallback?) {

        try {

            Thread(Runnable {
                responseHandler?.success(ArticleDao.fetchNextArticle(id.toInt()))
            }).start()

        } catch (e: Exception) {
            responseHandler?.error(e.message ?: "Something went wrong")
        }
    }

    fun fetchPrevArticle(id: String, responseHandler: ResponseCallback?) {

        try {

            Thread(Runnable {
                responseHandler?.success(ArticleDao.fetchPrevArticle(id.toInt()))
            }).start()

        } catch (e: Exception) {
            responseHandler?.error(e.message ?: "Something went wrong")
        }
    }

    fun disableArticle(id: String, responseHandler: ResponseCallback?) {

        try {

            Thread(Runnable {
                responseHandler?.success(ArticleDao.disableArticleById(id))
            }).start()

        } catch (e: Exception) {
            responseHandler?.error(e.message ?: "Something went wrong")
        }
    }

    fun enableArticle(id: String, responseHandler: ResponseCallback?) {

        try {

            Thread(Runnable {
                responseHandler?.success(ArticleDao.enableArticleById(id))
            }).start()

        } catch (e: Exception) {
            responseHandler?.error(e.message ?: "Something went wrong")
        }
    }
}