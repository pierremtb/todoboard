package com.pierrejacquier.todoboard.screens.board.di

import com.pierrejacquier.todoboard.di.TodoboardAppComponent
import com.pierrejacquier.todoboard.screens.board.BoardActivity
import com.pierrejacquier.todoboard.screens.board.ItemsManager
import dagger.Component

@Component(modules = arrayOf(BoardActivityModule::class), dependencies = arrayOf(TodoboardAppComponent::class))
@BoardActivityScope
interface BoardActivityComponent {

    fun inject(boardActivity: BoardActivity)

    fun getItemsManager(): ItemsManager
}