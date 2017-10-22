package com.pierrejacquier.todoboard.database.boards

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "boards")
data class Board(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val name: String
)