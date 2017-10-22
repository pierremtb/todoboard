package com.pierrejacquier.todoboard.database.boards

import android.os.Parcel
import android.os.Parcelable
import com.pierrejacquier.todoboard.commons.extensions.createParcel

data class BoardsParcel(var items: List<Board>): Parcelable {
    constructor(source: Parcel) : this(
            ArrayList<Board>().apply { source.readList(this, Board::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeList(items)
    }

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { BoardsParcel(it) }
    }
}