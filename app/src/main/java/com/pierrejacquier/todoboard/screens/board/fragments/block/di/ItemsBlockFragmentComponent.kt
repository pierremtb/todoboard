package com.pierrejacquier.todoboard.screens.board.fragments.block.di

import com.pierrejacquier.todoboard.di.TodoboardAppComponent
import com.pierrejacquier.todoboard.screens.board.di.BoardActivityComponent
import com.pierrejacquier.todoboard.screens.board.fragments.block.ItemsBlockFragment
import com.pierrejacquier.todoboard.screens.board.fragments.header.HeaderFragment
import com.pierrejacquier.todoboard.screens.board.fragments.header.di.HeaderFragmentModule
import com.pierrejacquier.todoboard.screens.board.fragments.header.di.HeaderFragmentScope
import dagger.Component

@Component(modules = arrayOf(ItemsBlockFragmentModule::class),
        dependencies = arrayOf(BoardActivityComponent::class))
@ItemsBlockFragmentScope
interface ItemsBlockFragmentComponent {

    fun inject(itemsBlockFragment: ItemsBlockFragment)
}