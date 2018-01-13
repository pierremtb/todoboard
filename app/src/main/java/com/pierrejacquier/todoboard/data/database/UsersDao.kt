package com.pierrejacquier.todoboard.data.database

import android.arch.persistence.room.*
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.todoist.User
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface UsersDao {

    @Query("SELECT * FROM users")
    fun getUsers(): Flowable<List<User>>

    @Query("SELECT EXISTS (SELECT * FROM users WHERE id = :id LIMIT 1)")
    fun isUser(id: Long): Single<Boolean>

    @Query("SELECT * FROM users WHERE id = :id")
    fun findUser(id: Long): Flowable<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(users: List<User>): Array<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg users: User): Array<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)
}