package com.pierrejacquier.todoboard

import android.app.Application
import com.pierrejacquier.todoboard.di.AppModule
import com.pierrejacquier.todoboard.di.boards.BoardsComponent
import com.pierrejacquier.todoboard.di.boards.DaggerBoardsComponent
import com.pierrejacquier.todoboard.di.setup.BoardSetupComponent
import com.pierrejacquier.todoboard.di.setup.DaggerBoardSetupComponent

class TodoboardApp : Application() {
    companion object {
        lateinit var boardsComponent: BoardsComponent
        lateinit var boardSetupComponent: BoardSetupComponent
    }

    override fun onCreate() {
        super.onCreate()
        boardsComponent = DaggerBoardsComponent.builder().appModule(AppModule(this)).build()
        boardSetupComponent = DaggerBoardSetupComponent.builder().appModule(AppModule(this)).build()
    }
}