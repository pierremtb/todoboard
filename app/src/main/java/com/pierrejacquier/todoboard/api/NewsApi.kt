package com.pierrejacquier.todoboard.api

import retrofit2.Call
import javax.inject.Inject

class NewsApi @Inject constructor(private val redditApi: RedditApi) : NewsApiBase {

    override fun getNews(after: String, limit: String): Call<RedditNewsResponse> {
        return redditApi.getTop(after, limit)
    }
}