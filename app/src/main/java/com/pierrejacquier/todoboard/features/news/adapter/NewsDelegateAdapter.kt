package com.pierrejacquier.todoboard.features.news.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.commons.RedditNewsItem
import com.pierrejacquier.todoboard.commons.adapter.ViewType
import com.pierrejacquier.todoboard.commons.adapter.ViewTypeDelegateAdapter
import com.pierrejacquier.todoboard.commons.extensions.getFriendlyTime
import com.pierrejacquier.todoboard.commons.extensions.inflate
import com.pierrejacquier.todoboard.commons.extensions.loadImg
import kotlinx.android.synthetic.main.news_item.view.*

class NewsDelegateAdapter : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return TurnsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as TurnsViewHolder
        holder.bind(item as RedditNewsItem)
    }

    class TurnsViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(parent.inflate(R.layout.news_item)) {
        fun bind(item: RedditNewsItem) = with(itemView) {
            img_thumbnail.loadImg(item.thumbnail)
            description.text = item.title
            author.text = item.author
            comments.text = "${item.numComments} comments"
            time.text = item.created.getFriendlyTime()
        }
    }

}