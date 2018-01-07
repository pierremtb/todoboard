package com.pierrejacquier.todoboard.data.database

import android.arch.persistence.room.*
import com.pierrejacquier.todoboard.data.model.todoist.Item
import com.pierrejacquier.todoboard.data.model.todoist.Project
import io.reactivex.Flowable

@Dao
abstract class ItemsDao {

    @Query("SELECT * FROM items")
    abstract fun getItems(): Flowable<List<Item>>

    @Query("SELECT * FROM items WHERE checked = 1")
    abstract fun getToDoItems(): Flowable<List<Item>>

    @Query("SELECT * FROM items WHERE checked = 0 AND projectId IN(:projectsIds)")
    abstract fun getBoardToDoItemsFromProjects(projectsIds: Array<Long?>): Flowable<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    abstract fun findItem(id: Long): Flowable<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertItem(item: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertItems(item: List<Item>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertItems(vararg item: Project)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateItem(item: Item)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateItems(item: List<Item>)

    @Delete
    abstract fun deleteItem(item: Item)

    @Delete
    abstract fun deleteItems(item: List<Item>)

    @Transaction
    open fun forceSyncBoardItems(boardId: Long, items: List<Item>) {
//        deleteBoardItems(boardId)
        insertItems(items)
    }
}