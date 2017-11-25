package com.pierrejacquier.todoboard.data.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.pierrejacquier.todoboard.data.model.todoist.TodoistAccessToken
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
@Entity(tableName = "boards")
data class Board(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var name: String,
    val accessToken: String,
    var userId: Long = 0,
    var syncToken: String = "*",
    val userName: String? = null
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelBoard.CREATOR
    }
}