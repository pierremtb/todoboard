package com.pierrejacquier.todoboard.screens.main.fragments.boards.di

import com.pierrejacquier.todoboard.di.TodoboardAppComponent
import com.pierrejacquier.todoboard.screens.main.fragments.boards.BoardsListFragment
import dagger.Component

@Component(modules = arrayOf(BoardsListFragmentModule::class), dependencies = arrayOf(TodoboardAppComponent::class))
@BoardsListFragmentScope
interface BoardsListFragmentComponent {

    fun inject(boardsListFragment: BoardsListFragment)
}