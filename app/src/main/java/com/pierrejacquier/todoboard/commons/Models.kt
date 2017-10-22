package com.pierrejacquier.todoboard.commons

import android.os.Parcel
import android.os.Parcelable
import com.pierrejacquier.todoboard.commons.adapter.AdapterConstants
import com.pierrejacquier.todoboard.commons.adapter.ViewType
import com.pierrejacquier.todoboard.commons.extensions.createParcel

data class RedditNews(
        val after: String,
        val before: String,
        val news: List<RedditNewsItem>) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            ArrayList<RedditNewsItem>().apply { source.readList(this, RedditNewsItem::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(after)
        writeString(before)
        writeList(news)
    }

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { RedditNews(it) }
    }
}

data class RedditNewsItem(
        val author: String,
        val title: String,
        val numComments: Int,
        val created: Long,
        val thumbnail: String,
        val url: String
) : ViewType, Parcelable {
    override fun getViewType() = AdapterConstants.BOARD

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readLong(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(author)
        writeString(title)
        writeInt(numComments)
        writeLong(created)
        writeString(thumbnail)
        writeString(url)
    }

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { RedditNewsItem(it) }
    }
}