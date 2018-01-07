package com.pierrejacquier.todoboard.data.database

import android.arch.persistence.room.*
import com.pierrejacquier.todoboard.data.model.BoardProjectJoin
import e
import io.reactivex.Flowable

@Dao
abstract class BoardProjectJoinsDao {

    @Query("SELECT * FROM boardProjectJoins")
    abstract fun getBoardProjectJoins(): Flowable<List<BoardProjectJoin>>

    @Query("SELECT * FROM boardProjectJoins WHERE id = :id")
    abstract fun findBoardProjectJoin(id: Long): Flowable<BoardProjectJoin>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBoardProjectJoin(boardProjectJoin: BoardProjectJoin): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBoardProjectJoins(boardProjectJoins: List<BoardProjectJoin>): Array<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBoardProjectJoins(vararg boardProjectJoins: BoardProjectJoin): Array<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateBoardProjectJoin(boardProjectJoin: BoardProjectJoin)

    @Delete
    abstract fun deleteBoardProjectJoin(boardProjectJoin: BoardProjectJoin)

    @Delete
    abstract fun deleteBoardProjectJoins(boardProjectJoins: List<BoardProjectJoin>)

    @Transaction
    open fun updateProjectsJoinsOfBoard(oldJoins: List<BoardProjectJoin>, newJoins: List<BoardProjectJoin>) {
        val newJoinsProjectId = newJoins.map { it.projectId }
        oldJoins.filter { !newJoinsProjectId.contains(it.projectId) }
        deleteBoardProjectJoins(oldJoins)
        insertBoardProjectJoins(newJoins)
    }
}