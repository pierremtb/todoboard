package com.pierrejacquier.todoboard.di.news

import dagger.Component
import com.pierrejacquier.todoboard.di.AppModule
import com.pierrejacquier.todoboard.di.NetworkModule
import com.pierrejacquier.todoboard.features.news.NewsFragment
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NewsModule::class, NetworkModule::class))
interface NewsComponent {
    fun inject(newsFragment: NewsFragment)
}