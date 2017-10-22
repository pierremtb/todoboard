//package com.pierrejacquier.todoboard.di.news
//
//import dagger.Module
//import dagger.Provides
//import com.pierrejacquier.todoboard.api.NewsApi
//import com.pierrejacquier.todoboard.api.NewsApiBase
//import com.pierrejacquier.todoboard.api.RedditApi
//import retrofit2.Retrofit
//import javax.inject.Singleton
//
//@Module
//class NewsModule {
//
//    @Provides
//    @Singleton
//    fun provideNewsApiBase(redditApi: RedditApi): NewsApiBase = NewsApi(redditApi)
//
//    @Provides
//    @Singleton
//    fun provideRedditApi(retrofit: Retrofit): RedditApi = retrofit.create(RedditApi::class.java)
//}