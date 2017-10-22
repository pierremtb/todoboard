package com.pierrejacquier.todoboard.di.boards

import com.pierrejacquier.todoboard.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BoardsModule {

    @Provides
    @Singleton
    fun providesBoardsDao(database: AppDatabase) = database.boardsDao()
//
//    @Provides
//    @Singleton
//    fun provideRedditApi(retrofit: Retrofit): RedditApi = retrofit.create(RedditApi::class.java)
}