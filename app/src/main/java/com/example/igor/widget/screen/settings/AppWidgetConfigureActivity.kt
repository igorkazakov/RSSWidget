package com.example.igor.widget.screen.settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.igor.widget.api.Repository
import com.example.igor.widget.api.models.Article
import com.example.igor.widget.R
import com.example.igor.widget.screen.widget.AppWidget.Companion.SETTINGS_CLICKED
import com.example.igor.widget.service.UpdateServiceManager
import com.example.igor.widget.utils.NetworkUtils
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

            UpdateServiceManager.startService(this)

            if (saveNewRssUrl()) {
                forceUpdateWidget()
                finish()
            }
        }
    }

    override fun onBackPressed() {

        if (mUrlEditText.text.toString().isEmpty()) {
            showErrorToast()
            return
        }

        super.onBackPressed()

        UpdateServiceManager.startService(this)
        val oldUrl = PreferencesUtils.instance.getRssUrl()
        val newUrl = mUrlEditText.text.toString()

        if (saveNewRssUrl() && oldUrl != newUrl) {
            updateWidgetWithNewUrl()

        } else {
            forceUpdateWidget()
        }
    }

    private fun forceUpdateWidget() {

        //TODO: need refactor!
        //без этого кода при создании виджета не обновится список
        val handler = Handler()
        handler.postDelayed({
            UpdateServiceManager.forceUpdateData(this)

        }, 2000)
    }

    private fun updateWidgetWithNewUrl() {

        UpdateServiceManager.updateDataWithNewRssUrl(this)
    }

    private fun initViews() {
        mRecyclerView = findViewById(R.id.recyclerView)
        mUrlEditText = findViewById(R.id.urlEditText)
        mAcceptButton = findViewById(R.id.acceptButton)
        mDisableTitle = findViewById(R.id.disableTitleText)

        mUrlEditText.setText(PreferencesUtils.instance.getRssUrl())
    }

    private fun saveNewRssUrl() : Boolean {

        val url = mUrlEditText.text.toString()

        if (NetworkUtils.isValidUrl(url)) {
            PreferencesUtils.instance.saveRssUrl(url)
            return true

        } else {
            showErrorToast()
            return false
        }
    }

    private fun showErrorToast() {
        Toast.makeText(this,
                resources.getString(R.string.url_error),
                Toast.LENGTH_SHORT).show()
    }

    private fun configureRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.layoutManager = linearLayoutManager
    }

    private fun showDisableArticles() {
        Repository.instance.fetchDisableArticles(object :
                Repository.ResponseCallback<List<Article>> {

            override fun success(response: List<Article>?) {

                if (response != null && response.isNotEmpty()) {
                    showDisabledArticles(response)

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
            Repository.instance.enableArticle(articleId, object :
                    Repository.ResponseCallback<Article> {

                override fun success(response: Article?) {
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

            Toast.makeText(this,
                    resources.getString(R.string.duplicate_widget_error),
                    Toast.LENGTH_SHORT).show()

            setResult(Activity.RESULT_CANCELED, resultValue)
            finish()

        } else {

            PreferencesUtils.instance.saveWidgetId(widgetID)
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
            setResult(Activity.RESULT_OK, resultValue)
        }
    }
}

