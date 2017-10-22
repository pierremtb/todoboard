package com.pierrejacquier.todoboard.api

import retrofit2.Call

interface NewsApiBase {
    fun getNews(after: String, limit: String): Call<RedditNewsResponse>
}