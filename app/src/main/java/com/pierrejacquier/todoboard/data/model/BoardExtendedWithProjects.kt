package com.pierrejacquier.todoboard.data.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.data.model.todoist.User
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
class BoardExtendedWithProjects : PaperParcelable {

    @Embedded
    var board: Board? = null

    @Relation(parentColumn = "userId", entityColumn = "id")
    var user: List<User> = ArrayList()

    @Relation(parentColumn = "id", entityColumn = "boardId", entity = BoardProjectJoin::class)
    var projectsJoins: List<BoardProjectJoinExt> = ArrayList()

    companion object {
        @JvmField val CREATOR = PaperParcelBoardExtendedWithProjects.CREATOR
    }
}