package com.pierrejacquier.todoboard.di

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit() = Retrofit.Builder()
            .baseUrl("https://todoist.com/api/v7/sync/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
}