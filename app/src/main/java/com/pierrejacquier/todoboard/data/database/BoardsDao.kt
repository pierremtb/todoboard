package com.pierrejacquier.todoboard.data.database

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.BoardWithUserName
import io.reactivex.Flowable
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@Dao
interface BoardsDao {

    @Query("SELECT * FROM boards")
    fun getBoards(): Flowable<List<Board>>

    @Query("SELECT boards.*, users.fullName AS userName FROM boards LEFT JOIN users ON boards.userId = users.id")
    fun getBoardsAndUserName(): Flowable<List<Board>>

    @Query("SELECT * FROM boards where id = :id")
    fun findBoard(id: Long): Flowable<List<Board>>

    @Insert(onConflict = REPLACE)
    fun insertBoard(board: Board): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBoards(boards: List<Board>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBoards(vararg boards: Board)

    @Update(onConflict = REPLACE)
    fun updateBoard(board: Board)

    @Delete
    fun deleteBoard(board: Board)
}