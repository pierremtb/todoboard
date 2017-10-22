package com.pierrejacquier.todoboard.api.auth

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TodoistApi {
    @POST("https://todoist.com/oauth/access_token")
    fun getAccessToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("code") code: String
    ): Call<AccessToken>
}