package com.pierrejacquier.todoboard.features.news

import com.pierrejacquier.todoboard.commons.RedditNewsItem
import com.pierrejacquier.todoboard.api.NewsApiBase
import com.pierrejacquier.todoboard.commons.RedditNews
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsManager @Inject constructor(private val api: NewsApiBase) {
    fun getNews(after: String, limit: String = "10"): Observable<RedditNews> {
        return Observable.create {
            subscriber ->

            val callResponse = api.getNews(after, limit)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val dataResponse = response.body()?.data
                if (dataResponse != null) {
                    val news = dataResponse.children.map {
                        val item = it.data
                        RedditNewsItem(item.author, item.title, item.num_comments, item.created, item.thumbnail, item.url)
                    }
                    val redditNews = RedditNews(dataResponse.after ?: "", dataResponse.before ?: "", news)
                    subscriber.onNext(redditNews)
                    subscriber.onCompleted()
                } else {
                    subscriber.onError(Throwable("Error"))
                }
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }
}