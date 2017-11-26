package com.pierrejacquier.todoboard.screens.details.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.commons.AutoUpdatableAdapter
import com.pierrejacquier.todoboard.commons.extensions.inflate
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.databinding.DetailsProjectItemBinding
import kotlin.properties.Delegates

class SelectedProjectsAdapter() : RecyclerView.Adapter<SelectedProjectsAdapter.ViewHolder>(), AutoUpdatableAdapter {

    var items: List<Project> by Delegates.observable(emptyList()) {
        _, old, new ->
        autoNotify(old, new) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                    DetailsProjectItemBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent, false
                    )
            )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(private val binding: DetailsProjectItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Project) = with(binding) {
            project = item
            executePendingBindings()
        }
    }
}