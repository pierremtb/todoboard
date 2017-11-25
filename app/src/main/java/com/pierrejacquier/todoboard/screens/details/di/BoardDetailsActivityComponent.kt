package com.pierrejacquier.todoboard.screens.details.di

import com.pierrejacquier.todoboard.di.TodoboardAppComponent
import com.pierrejacquier.todoboard.screens.details.BoardDetailsActivity
import dagger.Component

@Component(modules = arrayOf(BoardDetailsActivityModule::class), dependencies = arrayOf(TodoboardAppComponent::class))
@BoardDetailsActivityScope
interface BoardDetailsActivityComponent {

    fun inject(boardDetailsActivity: BoardDetailsActivity)
}