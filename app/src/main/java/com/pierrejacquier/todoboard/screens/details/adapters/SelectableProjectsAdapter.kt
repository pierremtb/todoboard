package com.pierrejacquier.todoboard.screens.details.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pierrejacquier.todoboard.commons.AutoUpdatableAdapter
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.databinding.ProjectCheckboxItemBinding
import com.pierrejacquier.todoboard.databinding.ProjectItemBinding
import kotlin.properties.Delegates

class SelectableProjectsAdapter() : RecyclerView.Adapter<SelectableProjectsAdapter.ViewHolder>(), AutoUpdatableAdapter {

    var items: List<Project> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                    ProjectCheckboxItemBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent, false
                    )
            )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(private val binding: ProjectCheckboxItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Project) = with(binding) {
            project = item
            executePendingBindings()
        }
    }
}