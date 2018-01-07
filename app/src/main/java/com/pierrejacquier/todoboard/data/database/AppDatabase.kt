package com.pierrejacquier.todoboard.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.BoardProjectJoin
import com.pierrejacquier.todoboard.data.model.todoist.Item
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.data.model.todoist.User

@Database(entities = arrayOf(Board::class, BoardProjectJoin::class, Project::class, Item::class, User::class),
        version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Local
    abstract fun boardsDao(): BoardsDao
    abstract fun boardProjectJoinsDao(): BoardProjectJoinsDao

    // Remote data
    abstract fun projectsDao(): ProjectsDao
    abstract fun itemsDao(): ItemsDao
    abstract fun usersDao(): UsersDao
}