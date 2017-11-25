package com.pierrejacquier.todoboard.di

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(val context: Context) {

    @Provides
    @TodoboardAppScope
    fun provideContext() = context
}