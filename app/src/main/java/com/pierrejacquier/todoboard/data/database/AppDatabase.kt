package com.pierrejacquier.todoboard.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.todoist.Item
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.data.model.todoist.User
import com.pierrejacquier.todoitem.data.database.ItemsDao

@Database(entities = arrayOf(Board::class, Project::class, Item::class, User::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun boardsDao(): BoardsDao
    abstract fun projectsDao(): ProjectsDao
    abstract fun itemsDao(): ItemsDao
    abstract fun usersDao(): UsersDao
}