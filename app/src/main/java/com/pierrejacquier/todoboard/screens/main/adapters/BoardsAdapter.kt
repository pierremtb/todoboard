package com.pierrejacquier.todoboard.screens.main.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pierrejacquier.todoboard.commons.AutoUpdatableAdapter
import com.pierrejacquier.todoboard.commons.extensions.log
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.BoardExtendedWithProjects
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.data.model.todoist.User
import com.pierrejacquier.todoboard.databinding.MainBoardItemBinding
import kotlinx.android.synthetic.main.main_board_item.view.*
import kotlin.properties.Delegates

class BoardsAdapter: RecyclerView.Adapter<BoardsAdapter.ViewHolder>(), AutoUpdatableAdapter {

    init {
        setHasStableIds(true)
        "oh hai".log()
    }

    var items: List<BoardExtendedWithProjects> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.board?.id == n.board?.id }
    }

    var projects: List<List<Project>> = emptyList()

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

    override fun getItemId(position: Int): Long {
        return items[position].board?.id!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        item.board?.let {
            holder.bind(
                    it,
                    item.user.getOrNull(0),
                    item.projectsJoins.mapNotNull { it.project.getOrNull(0) }.sortedBy { it.itemOrder }
            )

            with (holder.itemView) {
                holder.itemView.boardCard.setOnClickListener { onLaunchClick(item.board!!) }
                holder.itemView.launchBoardButton.setOnClickListener { onLaunchClick(item.board!!) }
                holder.itemView.configureBoardButton.setOnClickListener { onConfigureClick(item.board!!) }
            }
        }

    }

    class ViewHolder(private val binding: MainBoardItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currentBoard: Board, currentUser: User?, projects: List<Project>) = with(binding) {
            board = currentBoard
            user = currentUser
            firstProjects = projects.take(5).toList()
            projectsCount = "${projects.size} projects"

            executePendingBindings()
        }
    }
}