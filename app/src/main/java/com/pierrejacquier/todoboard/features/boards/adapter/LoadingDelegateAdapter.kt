package com.pierrejacquier.todoboard.features.boards.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.commons.adapter.ViewType
import com.pierrejacquier.todoboard.commons.adapter.ViewTypeDelegateAdapter
import com.pierrejacquier.todoboard.commons.extensions.inflate

class LoadingDelegateAdapter: ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup) = TurnsViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
    }

    class TurnsViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(parent.inflate(R.layout.fragment_boards))
}