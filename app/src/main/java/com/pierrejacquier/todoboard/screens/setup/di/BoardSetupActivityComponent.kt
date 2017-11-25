package com.pierrejacquier.todoboard.screens.setup.di

import com.pierrejacquier.todoboard.di.TodoboardAppComponent
import com.pierrejacquier.todoboard.screens.setup.BoardSetupActivity
import dagger.Component

@Component(modules = arrayOf(BoardSetupActivityModule::class), dependencies = arrayOf(TodoboardAppComponent::class))
@BoardSetupActivityScope
interface BoardSetupActivityComponent {

    fun inject(boardSetupActivity: BoardSetupActivity)
}