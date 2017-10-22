package com.pierrejacquier.todoboard.database.boards

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable

@Dao interface BoardsDao {

    @Query("select * from boards")
    fun getBoards(): Flowable<List<Board>>

    @Query("select * from boards where id = :id")
    fun findBoard(id: Long): Flowable<List<Board>>

    @Insert(onConflict = REPLACE)
    fun insertBoard(board: Board)

    @Update(onConflict = REPLACE)
    fun updateBoard(board: Board)

    @Delete
    fun deleteBoard(board: Board)
}