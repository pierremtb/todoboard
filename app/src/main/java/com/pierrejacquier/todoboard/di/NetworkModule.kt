package com.pierrejacquier.todoboard.di

import com.pierrejacquier.todoboard.data.api.SyncService
import com.pierrejacquier.todoboard.data.api.TodoistApi
import com.pierrejacquier.todoboard.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module(includes = arrayOf(DatabaseModule::class))
class NetworkModule {

    @Provides
    @TodoboardAppScope
    fun provideRetrofit() = Retrofit.Builder()
            .baseUrl("https://todoist.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    @TodoboardAppScope
    fun provideSyncService(todoistApi: TodoistApi, database: AppDatabase) =
            SyncService(todoistApi, database)

    @Provides
    @TodoboardAppScope
    fun provideTodoistApi(retrofit: Retrofit) = retrofit.create(TodoistApi::class.java) as TodoistApi
}