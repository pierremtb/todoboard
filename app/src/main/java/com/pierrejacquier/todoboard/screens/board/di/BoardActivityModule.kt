package com.pierrejacquier.todoboard.screens.board.di

import com.pierrejacquier.todoboard.screens.board.ItemsManager
import dagger.Module
import dagger.Provides

@Module
class BoardActivityModule {

    @Provides
    @BoardActivityScope
    fun providesItemsManager() = ItemsManager()
}