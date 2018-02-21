package com.pierrejacquier.todoboard.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.pierrejacquier.todoboard.screens.details.BoardDetailsActivity
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
@Entity(tableName = "boards")
data class Board(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String = "",
    var accessToken: String = "",
    var userId: Long = 0,
    var syncToken: String = "*",
    var userName: String? = null,
    var overdueEnabled: Boolean = true,
    var todayEnabled: Boolean = true,
    var tomorrowEnabled: Boolean = true,
    var laterEnabled: Boolean = true,
    var undatedEnabled: Boolean = true,
    var projectViewEnabled: Boolean = false,
    var order: Int = 0,
    var allowForMultiColumns: Boolean = true,
    var fontSize: Int = BoardDetailsActivity.DEFAULT_FONT_SIZE

): PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelBoard.CREATOR
    }

    fun getFontSizeString() = fontSize.toString()
}