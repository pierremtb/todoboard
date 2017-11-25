package com.pierrejacquier.todoboard.commons

import android.os.Parcel
import com.pierrejacquier.todoboard.data.model.todoist.TzInfo
import paperparcel.TypeAdapter

object TzInfoTypeAdapter: TypeAdapter<@JvmSuppressWildcards TzInfo> {
    override fun readFromParcel(source: Parcel): TzInfo {
        return TzInfo(
                source.readInt(),
                source.readString(),
                source.readInt(),
                source.readInt(),
                source.readString()
        )
    }

    override fun writeToParcel(value: TzInfo, dest: Parcel, flags: Int) {
        dest.writeInt(value.hours ?: 0)
        dest.writeString(value.timezone ?: "")
        dest.writeInt(value.isDst ?: 0)
        dest.writeInt(value.minutes ?: 0)
        dest.writeString(value.gmtString ?: "")
    }
}