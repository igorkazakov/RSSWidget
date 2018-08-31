package com.example.igor.widget.screen.settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.igor.rsswidjet.DataService.Repository
import com.example.igor.widget.DataService.models.Article

import com.example.igor.widget.R
import com.example.igor.widget.screen.widget.AppWidget
import com.example.igor.widget.screen.widget.AppWidget.Companion.SETTINGS_CLICKED
import com.example.igor.widget.service.UpdateService
import com.example.igor.widget.utils.PreferencesUtils

class AppWidgetConfigureActivity : Activity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mUrlEditText: EditText
    private lateinit var mAcceptButton: Button
    private lateinit var mDisableTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureActivityResult()

        setContentView(R.layout.preferences_layout)

        initViews()
        configureRecyclerView()
        showDisableArticles()

        mAcceptButton.setOnClickListener {

            forceUpdateWidget()
            saveNewRssUrl()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        forceUpdateWidget()
        Thread.sleep(1000)
        forceUpdateWidget()
        Thread.sleep(500)
        forceUpdateWidget()
    }

    private fun forceUpdateWidget() {

        val articleId = PreferencesUtils.instance.getCurrentArticleId()

        Repository.instance.fetchArticle(articleId.toString(), object : Repository.ResponseCallback {

            override fun success(response: Any?) {

                val article = response as? Article
                if (article != null) {

                    val intent = Intent(this@AppWidgetConfigureActivity,
                            AppWidget::class.java)
                    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    val ids = AppWidgetManager.getInstance(this@AppWidgetConfigureActivity)
                            .getAppWidgetIds(ComponentName(this@AppWidgetConfigureActivity,
                                    AppWidget::class.java))

                    intent.putExtra(UpdateService.UPDATE, true)
                    if (ids != null && ids.isNotEmpty()) {
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

                        val article = response as? Article
                        article?.let {
                            intent.putExtra(AppWidget.ARTICLE, article)
                        }

                        this@AppWidgetConfigureActivity.sendBroadcast(intent)
                    }
                }
            }

            override fun error(error: String) {}
        })



       // val widgetId = PreferencesUtils.instance.getWidgetId()
      //  AppWidget.updateCurrentArticle(this, null, widgetId)
    }

    private fun initViews() {
        mRecyclerView = findViewById(R.id.recyclerView)
        mUrlEditText = findViewById(R.id.urlEditText)
        mAcceptButton = findViewById(R.id.acceptButton)
        mDisableTitle = findViewById(R.id.disableTitleText)
    }

    private fun saveNewRssUrl() {
        PreferencesUtils.instance.saveRssUrl(mUrlEditText.text.toString())
        finish()
    }

    private fun configureRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.layoutManager = linearLayoutManager
    }

    private fun showDisableArticles() {
        Repository.instance.fetchDisableArticles(object : Repository.ResponseCallback {

            override fun success(response: Any?) {

                val list = response as? List<Article>
                if (list != null && list.isNotEmpty()) {
                    showDisabledArticles(list)

                } else {
                    hideDisabledArticlesList()
                }
            }

            override fun error(error: String) {
                hideDisabledArticlesList()
            }
        })
    }

    private fun hideDisabledArticlesList() {
        mRecyclerView.visibility = View.GONE
        mDisableTitle.visibility = View.GONE
    }

    private fun showDisabledArticles(articles: List<Article>) {

        val adapter = ArticlesAdapter(articles, mVenueAdapterListener)
        mRecyclerView.adapter = adapter
    }

    private val mVenueAdapterListener = object : ArticlesAdapter.ArticlesAdapterListener {
        override fun enableArticle(articleId: String) {
            Repository.instance.enableArticle(articleId, object : Repository.ResponseCallback {

                override fun success(response: Any?) {
                    showDisableArticles()
                }

                override fun error(error: String) {}
            })
        }
    }

    private fun configureActivityResult() {
        var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
        val intent = intent
        val extras = intent.extras

        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        val resultValue = Intent()

        if (extras.getBoolean(SETTINGS_CLICKED, false)) {

            setResult(Activity.RESULT_CANCELED, resultValue)

        } else if (PreferencesUtils.instance.getWidgetId() > 0) {

            Toast.makeText(this, "Widget already exists.", Toast.LENGTH_SHORT).show()

            setResult(Activity.RESULT_CANCELED, resultValue)
            finish()

        } else {

            //PreferencesUtils.instance.saveWidgetId(widgetID)
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            setResult(Activity.RESULT_OK, resultValue)
        }
    }
}

