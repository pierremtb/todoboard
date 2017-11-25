package com.pierrejacquier.todoboard.data.api

import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.data.model.todoist.TodoistAccessToken
import com.pierrejacquier.todoboard.data.model.todoist.TodoistSyncResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TodoistApi {
    @POST("/oauth/access_token")
    fun getAccessToken(
            @Query("client_id") clientId: String,
            @Query("client_secret") clientSecret: String,
            @Query("code") code: String
    ): Call<TodoistAccessToken>

    @GET("/api/v7/sync")
    fun sync(
            @Query("token") token: String,
            @Query("sync_token") syncToken: String,
            @Query("resource_types") resourceTypes: String = "[\"user\",\"projects\",\"items\"]"
    ): Observable<TodoistSyncResponse>
}