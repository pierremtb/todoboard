package com.pierrejacquier.todoboard.data.database

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.BoardExtended
import com.pierrejacquier.todoboard.data.model.BoardExtendedWithProjects
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface BoardsDao {

    @Query("SELECT * FROM boards")
    fun getBoards(): Flowable<List<Board>>

    @Query("SELECT * FROM boards")
    fun getBoardsExtended(): Flowable<List<BoardExtended>>

    @Query("SELECT * FROM boards")
    fun getBoardsExtendedWithProjects(): Flowable<List<BoardExtendedWithProjects>>

    @Query("SELECT * FROM boards WHERE id = :id")
    fun findBoardExtendedWithProjects(id: Long): Flowable<BoardExtendedWithProjects>

    @Query("SELECT * FROM boards WHERE id = :id")
    fun findBoardExtended(id: Long): Single<BoardExtended>

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
    fun deleteBoard(board: Board?)
}