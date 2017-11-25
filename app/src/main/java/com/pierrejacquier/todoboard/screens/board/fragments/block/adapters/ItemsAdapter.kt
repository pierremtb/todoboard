package com.pierrejacquier.todoboard.screens.board.fragments.block.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.gms.tasks.Task
import com.pierrejacquier.todoboard.commons.AutoUpdatableAdapter
import com.pierrejacquier.todoboard.commons.extensions.getDueTimeString
import com.pierrejacquier.todoboard.data.model.todoist.Item
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.databinding.ProjectItemBinding
import com.pierrejacquier.todoboard.databinding.TaskItemBinding
import kotlin.properties.Delegates

class ItemsAdapter() : RecyclerView.Adapter<ItemsAdapter.ViewHolder>(), AutoUpdatableAdapter {

    var items: List<Item> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                    TaskItemBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent, false
                    )
            )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(private val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) = with(binding) {
            task = item
            dueTime = item.getDueTimeString()
            executePendingBindings()
        }
    }
}