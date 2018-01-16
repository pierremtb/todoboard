package com.pierrejacquier.todoboard.commons

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

/**
 * From Antonio Leiva https://github.com/antoniolg/diffutil-recyclerview-kotlin/tree/master/app/src/main/java/com/antonioleiva/diffutilkotlin
 */

interface AutoUpdatableAdapter {

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
}