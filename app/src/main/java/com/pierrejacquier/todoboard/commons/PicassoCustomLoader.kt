package com.pierrejacquier.todoboard.commons

import agency.tango.android.avatarview.AvatarPlaceholder
import agency.tango.android.avatarview.loader.PicassoLoader
import agency.tango.android.avatarview.views.AvatarView
import com.squareup.picasso.Picasso

class PicassoCustomLoader(private val picasso: Picasso): PicassoLoader() {
    override fun loadImage(avatarView: AvatarView, avatarPlaceholder: AvatarPlaceholder, avatarUrl: String?) {
        picasso.load(avatarUrl)
                .placeholder(avatarPlaceholder)
                .fit()
                .into(avatarView)
    }
}