package com.pierrejacquier.todoboard.features.boards.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.commons.adapter.ViewType
import com.pierrejacquier.todoboard.commons.adapter.ViewTypeDelegateAdapter
import com.pierrejacquier.todoboard.commons.extensions.inflate
import com.pierrejacquier.todoboard.database.boards.Board
import kotlinx.android.synthetic.main.board_item.view.*

class BoardsDelegateAdapter : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return TurnsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as TurnsViewHolder
        holder.bind(item as Board)
    }

    class TurnsViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(parent.inflate(R.layout.board_item)) {
        fun bind(item: Board) = with(itemView) {
            boardName.text = item.name
        }
    }

}