package com.example.igor.widget.utils

import com.example.igor.widget.DataService.models.Article
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader


class RSSParser {

    companion object {
        fun parseXML(xml: String?) : List<Article> {

            val articles = mutableListOf<Article>()

            xml?.let {

                val factory = XmlPullParserFactory.newInstance()

                factory.isNamespaceAware = false
                val xmlPullParser = factory.newPullParser()

                xmlPullParser.setInput(StringReader(it))
                var insideItem = false
                var eventType = xmlPullParser.eventType
                var article = Article()

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xmlPullParser.name.equals("item", ignoreCase = true)) {
                            insideItem = true

                        } else if (xmlPullParser.name.equals("title", ignoreCase = true)) {
                            if (insideItem) {
                                article.title = xmlPullParser.nextText()
                            }

                        } else if (xmlPullParser.name.equals("link", ignoreCase = true)) {
                            if (insideItem) {
                                article.link = xmlPullParser.nextText()
                            }

                        } else if (xmlPullParser.name.equals("dc:creator", ignoreCase = true)) {
                            if (insideItem) {
                                article.author = xmlPullParser.nextText()
                            }

                        } else if (xmlPullParser.name.equals("content", ignoreCase = true)) {
                            if (insideItem) {
                                article.content = xmlPullParser.nextText()
                            }

                        } else if (xmlPullParser.name.equals("category", ignoreCase = true)) {
                            if (insideItem) {
                                article.category = xmlPullParser.nextText()
                            }

                        } else if (xmlPullParser.name.equals("description", ignoreCase = true)) {
                            if (insideItem) {
                                article.description = xmlPullParser.nextText()
                            }
                        }

                    } else if (eventType == XmlPullParser.END_TAG &&
                            xmlPullParser.name.equals("item", ignoreCase = true)) {
                        insideItem = false
                        articles.add(article)
                        article = Article()
                    }

                    eventType = xmlPullParser.next()
                }
            }

            return articles
        }
    }

}