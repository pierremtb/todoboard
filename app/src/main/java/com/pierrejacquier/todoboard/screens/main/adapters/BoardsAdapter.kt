package com.pierrejacquier.todoboard.screens.main.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pierrejacquier.todoboard.commons.AutoUpdatableAdapter
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.databinding.MainBoardItemBinding
import kotlinx.android.synthetic.main.main_board_item.view.*
import kotlin.properties.Delegates

class BoardsAdapter: RecyclerView.Adapter<BoardsAdapter.ViewHolder>(), AutoUpdatableAdapter {

    var items: List<Board> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id == n.id }
    }

    lateinit var onConfigureClick: (Board) -> Unit
    lateinit var onLaunchClick: (Board) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                    MainBoardItemBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent, false
                    )
            )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        with (holder.itemView) {
            holder.itemView.boardCard.setOnClickListener { onLaunchClick(item) }
            holder.itemView.launchBoardButton.setOnClickListener { onLaunchClick(item) }
            holder.itemView.configureBoardButton.setOnClickListener { onConfigureClick(item) }
        }
    }

    class ViewHolder(private val binding: MainBoardItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Board) = with(binding) {
            board = item
            executePendingBindings()
        }
    }
}