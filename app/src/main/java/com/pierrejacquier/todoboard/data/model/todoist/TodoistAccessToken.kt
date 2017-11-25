package com.pierrejacquier.todoboard.data.model.todoist

import android.arch.persistence.room.Entity
import com.squareup.moshi.Json

data class TodoistAccessToken(
        @Json(name = "access_token") val accessToken: String,
        @Json(name = "token_type") val tokenType: String
)