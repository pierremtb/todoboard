package com.pierrejacquier.todoboard.di.boards

import dagger.Component
import com.pierrejacquier.todoboard.di.AppModule
import com.pierrejacquier.todoboard.features.boards.BoardsFragment
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, BoardsModule::class))
interface BoardsComponent {
    fun inject(boardsFragment: BoardsFragment)
}