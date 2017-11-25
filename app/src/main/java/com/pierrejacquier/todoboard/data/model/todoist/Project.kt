package com.pierrejacquier.todoboard.data.model.todoist

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.pierrejacquier.todoboard.commons.Colors
import com.squareup.moshi.Json
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
@Entity(tableName = "projects")
data class Project(

        @PrimaryKey(autoGenerate = false)
        @Json(name = "id")
        var id: Long? = 0, //170467678

        var boardId: Long = 0,
		var userId: Long = 0,
		var selected: Boolean = true,

		@Json(name = "name") var name: String? , //Inbox
		@Json(name = "color") var color: Int , //7
		@Json(name = "is_deleted") var isDeleted: Int , //0
		@Json(name = "collapsed") var collapsed: Int? , //0
		@Json(name = "inbox_project") var inboxProject: Boolean? , //true
		@Json(name = "parent_id") var parentId: Long? , //null
		@Json(name = "item_order") var itemOrder: Int? , //0
		@Json(name = "indent") var indent: Int? , //1
		@Json(name = "shared") var shared: Boolean? , //false
		@Json(name = "is_archived") var isArchived: Int? = 0 //0
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelProject.CREATOR
    }

    fun getIntColor() = Colors.getColor(this.color)
}