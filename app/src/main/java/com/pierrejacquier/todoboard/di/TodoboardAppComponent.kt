package com.pierrejacquier.todoboard.di

import com.pierrejacquier.todoboard.data.api.SyncService
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.database.BoardsDao
import com.pierrejacquier.todoboard.data.database.ProjectsDao
import com.squareup.picasso.Picasso
import dagger.Component
import retrofit2.Retrofit

@Component(modules = arrayOf(DatabaseModule::class, NetworkModule::class, PicassoModule::class))
@TodoboardAppScope
interface TodoboardAppComponent {

    fun getDatabase(): AppDatabase

    fun getSyncService(): SyncService

    fun getPicasso(): Picasso
}