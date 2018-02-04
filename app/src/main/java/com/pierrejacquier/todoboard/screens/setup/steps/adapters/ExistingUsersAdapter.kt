package com.pierrejacquier.todoboard.screens.setup.steps.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.commons.AutoUpdatableAdapter
import com.pierrejacquier.todoboard.commons.CircleTransform
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.todoist.User
import com.pierrejacquier.todoboard.databinding.SetupUserItemBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.main_fragment_boards.view.*
import kotlin.properties.Delegates


class ExistingUsersAdapter(private val picasso: Picasso): RecyclerView.Adapter<ExistingUsersAdapter.ViewHolder>(), AutoUpdatableAdapter {

    init {
        setHasStableIds(true)
    }

    var items: List<User> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id == n.id }
    }

    lateinit var onClick: (User) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                    SetupUserItemBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent, false
                    ),
                    picasso
            )

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onClick)
    }

    class ViewHolder(private val binding: SetupUserItemBinding, private val picasso: Picasso) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: User, onClick: (User) -> Unit) = with(binding) {
            user = item
            picasso.load(item.avatarMedium)
                    .placeholder(R.drawable.ic_account_circle)
                    .transform(CircleTransform())
                    .into(userRow.avatarView)
            userRow.setOnClickListener { onClick(item) }
            executePendingBindings()
        }
    }
}