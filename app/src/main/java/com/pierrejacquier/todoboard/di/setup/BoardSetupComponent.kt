package com.pierrejacquier.todoboard.di.setup

import com.pierrejacquier.todoboard.BoardSetupActivity
import com.pierrejacquier.todoboard.di.AppModule
import com.pierrejacquier.todoboard.di.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, BoardSetupModule::class, NetworkModule::class))
interface BoardSetupComponent {

    fun inject(boardSetupActivity: BoardSetupActivity)
}