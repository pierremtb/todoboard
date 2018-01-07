@file:JvmName("ExtensionsUtils")

package com.pierrejacquier.todoboard.commons.extensions

import android.content.Context
import android.databinding.ObservableList
import android.os.Debug
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.data.model.todoist.Item
import e
import khronos.toString
import java.text.SimpleDateFormat
import java.util.*

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun Int.toBool(): Boolean = this == 1

fun Int.dp(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

fun Any.log() {
    e { this.toString() }
}

fun Item.getDueDate(): Date {
    val simpleDateFormat = SimpleDateFormat("EEE dd MMM yyyy hh:mm:ss Z")
    simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return simpleDateFormat.parse(dueDateUtc)
}

fun Item.getDueTimeString(): String {
    if (dueDateUtc == null) {
        return ""
    }
    if (allDay != null) {
        if (allDay) {
            return ""
        }
    }
    return getDueDate().toString("hh:mm")
}

fun <T> RecyclerView.Adapter<*>.autoNotify(old: List<T>, new: List<T>, compare: (T, T) -> Boolean) {
    val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return compare(old[oldItemPosition], new[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }

        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size
    })

    diff.dispatchUpdatesTo(this)
}

// Inline function to create Parcel Creator
inline fun <reified T : Parcelable> createParcel(
                crossinline createFromParcel: (Parcel) -> T?): Parcelable.Creator<T> =
                    object : Parcelable.Creator<T> {
                            override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
                            override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
                        }
