package com.example.igor.widget.db.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.example.igor.widget.api.models.Article
import com.example.igor.widget.db.DbManager


object ArticleDao {

    private const val TABLE_NAME = "Article"
    private const val TITLE = "title"
    private const val AUTHOR = "author"
    private const val LINK = "link"
    private const val DESCRIPTION = "description"
    private const val CONTENT = "content"
    private const val CATEGORY = "category"
    private const val DISABLE = "disable"

    fun createTable(): String {
        return "CREATE TABLE $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$TITLE TEXT, " +
                "$AUTHOR TEXT, " +
                "$LINK TEXT, " +
                "$DESCRIPTION TEXT, " +
                "$CONTENT TEXT, " +
                "$DISABLE TEXT, " +
                "$CATEGORY TEXT); "
    }

    fun removeTable(): String {
        return "DROP TABLE IF EXISTS $TABLE_NAME "
    }

    fun insertOrUpdate(articles: List<Article>) {

        val database = DbManager.instance.openDatabase()

        database?.let {

            it.beginTransaction()
            try {

                for (item in articles) {
                    if (getArticleById(item.id, it) != null) {
                        update(item, it)

                    } else {
                        insert(item, it)
                    }
                }

                it.setTransactionSuccessful()

            } finally {
                it.endTransaction()
            }
        }

        DbManager.instance.closeDatabase()
    }

    fun deleteAllAndUpdate(articles: List<Article>) {

        val database = DbManager.instance.openDatabase()

        database?.let {

            it.beginTransaction()
            try {

                deleteAll(database)

                for (item in articles) {
                    insert(item, it)
                }

                it.setTransactionSuccessful()

            } finally {
                it.endTransaction()
            }
        }

        DbManager.instance.closeDatabase()
    }

    fun fetchAllArticles(): List<Article> {
        val database = DbManager.instance.openDatabase()
        val result: MutableList<Article> = mutableListOf()

        database?.let {

            it.beginTransaction()
            try {

                val cursor = it.rawQuery(
                        "SELECT * FROM $TABLE_NAME ", arrayOf())

                if (cursor.moveToFirst()) {

                    do {

                        val item = Article(cursor)
                        result.add(item)

                    } while (cursor.moveToNext())

                    cursor.close()
                }

                it.setTransactionSuccessful()

            } finally {
                it.endTransaction()
            }
        }

        DbManager.instance.closeDatabase()

        return result
    }

    fun fetchDisableArticles(): List<Article> {
        val database = DbManager.instance.openDatabase()
        val result: MutableList<Article> = mutableListOf()

        database?.let {

            it.beginTransaction()
            try {

                val cursor = it.rawQuery(
                        "SELECT * FROM $TABLE_NAME WHERE $DISABLE = 1 ", arrayOf())

                if (cursor.moveToFirst()) {

                    do {

                        val item = Article(cursor)
                        result.add(item)

                    } while (cursor.moveToNext())

                    cursor.close()
                }

                it.setTransactionSuccessful()

            } finally {
                it.endTransaction()
            }
        }

        DbManager.instance.closeDatabase()

        return result
    }

    fun fetchNextArticle(currentId: Int): Article? {
        val database = DbManager.instance.openDatabase()
        var result: Article? = null

        database?.let {

            it.beginTransaction()
            try {

                val cursor = it.rawQuery(
                        "SELECT * FROM $TABLE_NAME" +
                                " WHERE $currentId < ${BaseColumns._ID} AND " +
                                "$DISABLE = 0 ORDER BY ${BaseColumns._ID} ", arrayOf())

                if (cursor.moveToFirst()) {
                    result = Article(cursor)
                    cursor.close()
                }

                it.setTransactionSuccessful()

            } finally {
                it.endTransaction()
            }
        }

        DbManager.instance.closeDatabase()

        return result
    }

    fun fetchPrevArticle(currentId: Int): Article? {
        val database = DbManager.instance.openDatabase()
        var result: Article? = null

        database?.let {

            it.beginTransaction()
            try {

                val cursor = it.rawQuery(
                        "SELECT * FROM $TABLE_NAME" +
                                " WHERE $currentId > ${BaseColumns._ID} AND " +
                                "$DISABLE = 0 ORDER BY ${BaseColumns._ID} DESC ", arrayOf())

                if (cursor.moveToFirst()) {
                    result = Article(cursor)
                    cursor.close()
                }

                it.setTransactionSuccessful()

            } finally {
                it.endTransaction()
            }
        }

        DbManager.instance.closeDatabase()

        return result
    }

    fun getArticleById(id: String?): Article? {

        val database = DbManager.instance.openDatabase()
        var article: Article? = null

        database?.let {
            article = getArticleById(id, database)
        }

        DbManager.instance.closeDatabase()

        return article
    }

    fun disableArticleById(id: String?): Article? {

        if (id == null) {
            return null
        }

        val database = DbManager.instance.openDatabase()
        var article: Article? = null

        database?.let {
            article = getArticleById(id, database)
            article?.let {
                it.disable = 1
                update(it, database)
            }

            article = fetchNextArticle(id.toInt())

            if (article == null) {
                article = fetchPrevArticle(id.toInt())
            }
        }

        DbManager.instance.closeDatabase()

        return article
    }

    fun enableArticleById(id: String?): Article? {

        if (id == null) {
            return null
        }

        val database = DbManager.instance.openDatabase()
        var article: Article? = null

        database?.let {
            article = getArticleById(id, database)
            article?.let {
                it.disable = 0
                update(it, database)
            }

            article = fetchNextArticle(id.toInt())

            if (article == null) {
                article = fetchPrevArticle(id.toInt())
            }
        }

        DbManager.instance.closeDatabase()

        return article
    }

    private fun getArticleById(id: String?, database: SQLiteDatabase): Article? {

        id?.let {

            val cursor = database.rawQuery(
                    "SELECT * FROM $TABLE_NAME WHERE _id = ?", arrayOf(id))

            if (cursor.moveToFirst()) {
                val result = Article(cursor)
                cursor.close()
                return result
            }
        }

        return null
    }

    private fun insert(article: Article, database: SQLiteDatabase) {

        val values = ContentValues()
        values.put(TITLE, article.title)
        values.put(AUTHOR, article.author)
        values.put(LINK, article.link)
        values.put(DESCRIPTION, article.description)
        values.put(CONTENT, article.content)
        values.put(DISABLE, 0)
        values.put(CATEGORY, article.category)

        database.insert(TABLE_NAME, null, values)
    }

    private fun update(article: Article, database: SQLiteDatabase) {

        article.id?.let {

            val values = ContentValues()
            values.put(TITLE, article.title)
            values.put(AUTHOR, article.author)
            values.put(LINK, article.link)
            values.put(DESCRIPTION, article.description)
            values.put(CONTENT, article.content)
            values.put(DISABLE, article.disable)
            values.put(CATEGORY, article.category)

            database.update(TABLE_NAME, values, "${BaseColumns._ID} = ?", arrayOf(article.id))
        }
    }

    private fun deleteAll(database: SQLiteDatabase) {

        database.rawQuery(
                "DELETE FROM $TABLE_NAME ", arrayOf())
    }
}