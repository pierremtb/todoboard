package com.pierrejacquier.todoboard.di.setup

import com.pierrejacquier.todoboard.api.auth.TodoistApi
import com.pierrejacquier.todoboard.di.AppModule
import com.pierrejacquier.todoboard.di.NetworkModule
import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class BoardSetupModule {

    @Provides
    @Singleton
    fun provideTodoistApi(retrofit: Retrofit): TodoistApi = retrofit.create(TodoistApi::class.java)
}