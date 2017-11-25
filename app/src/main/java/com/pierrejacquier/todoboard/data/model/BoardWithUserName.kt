package com.pierrejacquier.todoboard.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import paperparcel.PaperParcel
import paperparcel.PaperParcelable


@PaperParcel
@Entity(tableName = "boards")
data class BoardWithUserName(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        var name: String,
        val accessToken: String,
        var userId: Long = 0,
        var syncToken: String = "*",
        val userName: String = ""
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelBoardWithUserName.CREATOR
    }
}