package com.pierrejacquier.todoboard.di

import android.content.Context
import dagger.Module
import dagger.Provides
import com.pierrejacquier.todoboard.TodoboardApp
import javax.inject.Singleton

@Module
class AppModule(val app: TodoboardApp) {
    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    @Singleton
    fun provideApplication(): TodoboardApp = app
}