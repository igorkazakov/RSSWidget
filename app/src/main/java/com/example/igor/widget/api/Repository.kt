package com.example.igor.widget.api

import android.os.Handler
import android.os.Looper
import com.example.igor.widget.api.models.Article
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

    interface ResponseCallback<in T> {
        fun success(response: T?)
        fun error(error: String)
    }

    fun loadRss(url: String?, updateUrl: Boolean, responseHandler: ResponseCallback<Article>?) {

        if (url != null && url.isNotEmpty()) {

            Thread(Runnable {

                var article: Article? = null
                val articleList: List<Article>

                try {

                    val result = URL(url).readText()
                    val array = RSSParser.parseXML(result)

                    if (updateUrl) {
                        ArticleDao.deleteAllAndUpdate(array)

                    } else {
                        ArticleDao.insertOrUpdate(array)
                    }

                    articleList = ArticleDao.fetchAllArticles()

                } catch (e: Exception) {
                    responseHandler?.error(e.message ?: "Something went wrong")
                    return@Runnable
                }

                if (articleList.isNotEmpty()) {
                    article = articleList.first()
                }

                responseHandler?.success(article)

            }).start()

        } else {
            responseHandler?.error("Url is empty!")
        }
    }

    fun fetchDisableArticles(responseHandler: ResponseCallback<List<Article>>?) {

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

    fun fetchArticle(id: String, responseHandler: ResponseCallback<Article>?) {

        try {

            Thread(Runnable {
                responseHandler?.success(ArticleDao.getArticleById(id))
            }).start()

        } catch (e: Exception) {
            responseHandler?.error(e.message ?: "Something went wrong")
        }
    }

    fun fetchNextArticle(id: String, responseHandler: ResponseCallback<Article>?) {

        try {

            Thread(Runnable {
                responseHandler?.success(ArticleDao.fetchNextArticle(id.toInt()))
            }).start()

        } catch (e: Exception) {
            responseHandler?.error(e.message ?: "Something went wrong")
        }
    }

    fun fetchPrevArticle(id: String, responseHandler: ResponseCallback<Article>?) {

        try {

            Thread(Runnable {
                responseHandler?.success(ArticleDao.fetchPrevArticle(id.toInt()))
            }).start()

        } catch (e: Exception) {
            responseHandler?.error(e.message ?: "Something went wrong")
        }
    }

    fun disableArticle(id: String, responseHandler: ResponseCallback<Article>?) {

        try {

            Thread(Runnable {
                responseHandler?.success(ArticleDao.disableArticleById(id))
            }).start()

        } catch (e: Exception) {
            responseHandler?.error(e.message ?: "Something went wrong")
        }
    }

    fun enableArticle(id: String, responseHandler: ResponseCallback<Article>?) {

        try {

            Thread(Runnable {
                responseHandler?.success(ArticleDao.enableArticleById(id))
            }).start()

        } catch (e: Exception) {
            responseHandler?.error(e.message ?: "Something went wrong")
        }
    }
}