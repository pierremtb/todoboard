package com.pierrejacquier.todoboard.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.pierrejacquier.todoboard.database.boards.Board
import com.pierrejacquier.todoboard.database.boards.BoardsDao
import dagger.Provides

@Database(entities = arrayOf(Board::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun boardsDao(): BoardsDao
}