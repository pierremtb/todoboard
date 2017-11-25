package com.pierrejacquier.todoboard.data.model.todoist
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "items")
data class Item(

        @PrimaryKey(autoGenerate = false)
        @Json(name = "id") val id: Long?, //2391052307

        var boardId: Long = 0,

        @Json(name = "user_id") val userId: Long?, //7833481

        @Json(name = "day_order") val dayOrder: Int?, //1
        @Json(name = "assigned_by_uid") val assignedByUid: String?, //null
        @Json(name = "is_archived") val isArchived: Int?, //0
//        @Json(name = "labels") val labels: List<Long?>?,
        @Json(name = "sync_id") val syncId: Long?, //null
        @Json(name = "all_day") val allDay: Boolean?, //false
        @Json(name = "in_history") val inHistory: Int?, //0
        @Json(name = "date_added") val dateAdded: String?, //Tue 07 Nov 2017 03:34:59 +0000
        @Json(name = "indent") val indent: Int?, //1
        @Json(name = "date_lang") val dateLang: String?, //en
        @Json(name = "content") val content: String?, //Memrise (+prepare for bed dumbass)
        @Json(name = "checked") val checked: Int?, //0
        @Json(name = "due_date_utc") val dueDateUtc: String?, //Fri 17 Nov 2017 14:15:00 +0000
        @Json(name = "priority") val priority: Int?, //1
        @Json(name = "parent_id") val parentId: Long?, //null
        @Json(name = "item_order") val itemOrder: Int?, //9
        @Json(name = "is_deleted") val isDeleted: Int, //0
        @Json(name = "responsible_uid") val responsibleUid: String?, //null
        @Json(name = "project_id") val projectId: Long?, //2156294426
        @Json(name = "collapsed") val collapsed: Int?, //0
        @Json(name = "date_string") val dateString: String? //every day at 10:15pm
)