package com.pierrejacquier.todoboard.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.pierrejacquier.todoboard.data.model.todoist.Project
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
@Entity(tableName = "boardProjectJoins",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Board::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("boardId"),
                        onDelete = ForeignKey.CASCADE
                ),
                ForeignKey(
                        entity = Project::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("projectId"),
                        onDelete = ForeignKey.NO_ACTION
                )
        )
)
data class BoardProjectJoin(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var boardId: Long = 0,
    var projectId: Long = 0
): PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelBoardProjectJoin.CREATOR
    }
}