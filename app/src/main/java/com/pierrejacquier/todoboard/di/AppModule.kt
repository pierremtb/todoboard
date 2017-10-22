package com.pierrejacquier.todoboard.di

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import com.pierrejacquier.todoboard.TodoboardApp
import com.pierrejacquier.todoboard.database.AppDatabase
import javax.inject.Singleton

@Module
class AppModule(val app: TodoboardApp) {
    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    @Singleton
    fun provideApplication(): TodoboardApp = app

    @Provides
    @Singleton
    fun providesAppDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "todoboard-db").build()

}