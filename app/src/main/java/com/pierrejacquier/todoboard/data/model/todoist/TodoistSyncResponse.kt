package com.pierrejacquier.todoboard.data.model.todoist

import com.squareup.moshi.Json

data class TodoistSyncResponse(
		@Json(name = "user") val user: User?,
		@Json(name = "items") val items: List<Item> = emptyList(),
		@Json(name = "projects") val projects: List<Project> = emptyList(),
		@Json(name = "full_sync") val fullSync: Boolean, //true
		@Json(name = "sync_token") val syncToken: String //mxQJb6fU39iwvPwzB3GPQVIoKyWxNz99dSHjJJnlnm6IJ7_fI1aLGNK_HBSPRKCXlyaK8m7SAuUDP-e81SfhTRbUQmlsv2Nu6rfpw7TDEtIw6yU

)

