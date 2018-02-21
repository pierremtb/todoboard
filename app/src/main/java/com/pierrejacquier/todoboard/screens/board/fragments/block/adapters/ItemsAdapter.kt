package com.pierrejacquier.todoboard.screens.board.fragments.block.adapters

import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pierrejacquier.todoboard.commons.AutoUpdatableAdapter
import com.pierrejacquier.todoboard.commons.extensions.dp
import com.pierrejacquier.todoboard.commons.extensions.getDueTimeString
import com.pierrejacquier.todoboard.data.model.todoist.Item
import com.pierrejacquier.todoboard.databinding.BoardTaskItemBinding
import kotlin.properties.Delegates


class ItemsAdapter(var screenWidth: Int, private val fontSize: Int): RecyclerView.Adapter<ItemsAdapter.ViewHolder>(), AutoUpdatableAdapter {

    init {
        setHasStableIds(true)
    }

    var items: List<Item> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                    BoardTaskItemBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent, false
                    )
            )

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], screenWidth, fontSize)
    }

    class ViewHolder(private val binding: BoardTaskItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, screenWidth: Int, fontSize: Int) = with(binding) {
            task = item
            dueTime = item.getDueTimeString()
            textView.maxWidth = if (dueTime.isNullOrEmpty()) screenWidth - 164 else screenWidth - 199
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.toFloat())
            binding.root.layoutParams.height = fontSize + 20.dp(root.context)
            executePendingBindings()
        }
    }
}