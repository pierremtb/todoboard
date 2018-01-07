package com.pierrejacquier.todoboard.data.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.pierrejacquier.todoboard.data.model.todoist.User
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
class BoardExtended : PaperParcelable {

    @Embedded
    var board: Board? = null

    @Relation(parentColumn = "userId", entityColumn = "id")
    var user: List<User> = ArrayList()

    @Relation(parentColumn = "id", entityColumn = "boardId")
    var projectsJoins: List<BoardProjectJoin> = ArrayList()

    companion object {
        @JvmField val CREATOR = PaperParcelBoardExtended.CREATOR
    }
}