package com.pierrejacquier.todoboard.data.database

import android.arch.persistence.room.*
import com.pierrejacquier.todoboard.data.model.todoist.Project
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class ProjectsDao {

    @Query("SELECT * FROM projects")
    abstract fun getProjects(): Flowable<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id")
    abstract fun findProject(id: Long): Flowable<List<Project>>

    @Query("SELECT * FROM projects WHERE boardId = :boardId")
    abstract fun findBoardProjects(boardId: Long): Flowable<List<Project>>

    @Query("SELECT * FROM projects WHERE boardId = :boardId")
    abstract fun findBoardProjectsSingle(boardId: Long): Single<List<Project>>

    @Query("SELECT * FROM projects WHERE userId = :userId")
    abstract fun findUserProjects(userId: Long): Flowable<List<Project>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertProject(project: Project)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertProjects(projects: List<Project>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertProjects(vararg project: Project)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateProject(project: Project)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateProjects(projects: List<Project>)

    @Delete
    abstract fun deleteProject(project: Project)

    @Delete
    abstract fun deleteProjects(projects: List<Project>)

    @Query("DELETE FROM projects WHERE boardId = :boardId")
    abstract fun deleteBoardProjects(boardId: Long)

    open fun forceSyncBoardProjects(boardId: Long, projects: List<Project>) {
        deleteBoardProjects(boardId)
        insertProjects(projects)
    }
}