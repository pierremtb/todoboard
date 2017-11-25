package com.pierrejacquier.todoboard.screens.board.fragments.header.di

import com.pierrejacquier.todoboard.di.TodoboardAppComponent
import com.pierrejacquier.todoboard.screens.board.di.BoardActivityComponent
import com.pierrejacquier.todoboard.screens.board.fragments.header.HeaderFragment
import dagger.Component

@Component(modules = arrayOf(HeaderFragmentModule::class), dependencies = arrayOf(TodoboardAppComponent::class))
@HeaderFragmentScope
interface HeaderFragmentComponent {

    fun inject(headerFragment: HeaderFragment)
}