package com.pierrejacquier.todoboard.di

import android.arch.persistence.room.Room
import android.content.Context
import com.pierrejacquier.todoboard.data.database.AppDatabase
import dagger.Component
import dagger.Module
import dagger.Provides

@Module(includes = arrayOf(ContextModule::class))
class DatabaseModule {

    @Provides
    @TodoboardAppScope
    fun provideDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "todoboard-db").build()
}