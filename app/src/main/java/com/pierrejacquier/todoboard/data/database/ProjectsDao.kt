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

    @Query("SELECT * FROM projects WHERE id IN(:ids)")
    abstract fun findProjects(ids: Array<Long>): Flowable<List<Project>>

    @Query("SELECT * FROM projects WHERE id IN(:ids)")
    abstract fun findProjectsSingle(ids: Array<Long>): Single<List<Project>>

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

    @Transaction
    open fun forceSyncBoardProjects(boardId: Long, projects: List<Project>) {
//        deleteBoardProjects(boardId)
        insertProjects(projects)
    }
}