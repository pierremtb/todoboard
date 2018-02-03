package com.pierrejacquier.todoboard.screens.board.fragments

import android.support.v7.util.DiffUtil
import com.pierrejacquier.todoboard.data.model.todoist.Item


class ItemsDiffCallback(private val oldItems: List<Item>, private val newItems: List<Item>): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldItems[oldItemPosition]
        val new = newItems[newItemPosition]

        return old.content == new.content
    }

//    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
//        // Implement method if you're going to use ItemAnimator
//        return super.getChangePayload(oldItemPosition, newItemPosition)
//    }
}