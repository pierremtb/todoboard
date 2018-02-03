package com.pierrejacquier.todoboard.screens.board.fragments.block.di

import com.pierrejacquier.todoboard.screens.board.di.BoardActivityComponent
import com.pierrejacquier.todoboard.screens.board.fragments.project.ProjectBlockFragment
import com.pierrejacquier.todoboard.screens.board.fragments.project.di.ProjectBlockFragmentScope
import dagger.Component

@Component(modules = arrayOf(ProjectBlockFragmentModule::class),
        dependencies = arrayOf(BoardActivityComponent::class))
@ProjectBlockFragmentScope
interface ProjectBlockFragmentComponent {

    fun inject(projectBlockFragment: ProjectBlockFragment)
}