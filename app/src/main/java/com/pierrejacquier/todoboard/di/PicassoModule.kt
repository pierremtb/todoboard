package com.pierrejacquier.todoboard.di

import android.content.Context
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides

@Module(includes = arrayOf(ContextModule::class))
class PicassoModule {

    @Provides
    @TodoboardAppScope
    fun providePicasso(context: Context) = Picasso.Builder(context).build()
}