package com.pierrejacquier.todoboard;

import android.app.Application;
import com.pierrejacquier.todoboard.di.AppModule
import com.pierrejacquier.todoboard.di.news.DaggerNewsComponent
import com.pierrejacquier.todoboard.di.news.NewsComponent

class TodoboardApp : Application() {
    companion object {
        lateinit var newsComponent: NewsComponent
    }

    override fun onCreate() {
        super.onCreate()
        newsComponent = DaggerNewsComponent.builder().appModule(AppModule(this)).build()
    }
}