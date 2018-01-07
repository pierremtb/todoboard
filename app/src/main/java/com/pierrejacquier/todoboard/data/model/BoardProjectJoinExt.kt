package com.pierrejacquier.todoboard.data.model

import android.arch.persistence.room.*
import com.pierrejacquier.todoboard.data.model.todoist.Project
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
data class BoardProjectJoinExt(
    @Embedded
    var boardProjectJoin: BoardProjectJoin? = null,

    @Relation(parentColumn = "projectId", entityColumn = "id")
    var project: List<Project> = ArrayList()

): PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelBoardProjectJoinExt.CREATOR
    }
}