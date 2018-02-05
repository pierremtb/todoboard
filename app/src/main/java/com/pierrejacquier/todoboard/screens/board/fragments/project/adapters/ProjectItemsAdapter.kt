package com.pierrejacquier.todoboard.screens.board.fragments.project.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pierrejacquier.todoboard.commons.AutoUpdatableAdapter
import com.pierrejacquier.todoboard.commons.extensions.*
import com.pierrejacquier.todoboard.data.model.todoist.Item
import com.pierrejacquier.todoboard.databinding.BoardProjectTaskItemBinding
import kotlin.properties.Delegates


class ProjectItemsAdapter(var screenWidth: Int): RecyclerView.Adapter<ProjectItemsAdapter.ViewHolder>(), AutoUpdatableAdapter {

    init {
        setHasStableIds(true)
    }

    companion object {
        const val CONTENT_LEFT_OFFSET = 12 + 16
    }

    var items: List<Item> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                    BoardProjectTaskItemBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent, false
                    )
            )

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], screenWidth)
    }

    class ViewHolder(private val binding: BoardProjectTaskItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, screenWidth: Int) = with(binding) {
            task = item
            dueTime = item.getDueDateTimeString()

            val indentWidth = (item.indent!! - 1) * CONTENT_LEFT_OFFSET

            textView.maxWidth =
                    if (dueTime.isNullOrEmpty())
                        screenWidth - (indentWidth + 16*2 + 40).dp(binding.root.context)
                    else
                        screenWidth - (indentWidth + 16*2 + 40 + 13*8).dp(binding.root.context)

            indenter.layoutParams.width = indentWidth.dp(binding.root.context)
            executePendingBindings()
        }
    }
}