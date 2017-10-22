package com.pierrejacquier.todoboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.pierrejacquier.todoboard.features.setup.SetupFragmentStepAdapter
import kotlinx.android.synthetic.main.activity_display_setup.*
import android.R.id.edit
import android.content.SharedPreferences
import android.util.Log
import com.pierrejacquier.todoboard.api.auth.AccessToken
import com.pierrejacquier.todoboard.api.auth.TodoistApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject


fun Context.DisplaySetupIntent() = Intent(this, BoardSetupActivity::class.java)

class BoardSetupActivity : AppCompatActivity() {

    companion object {
        val API_LOGIN_URL = "https://todoist.com/oauth/authorize"
        val API_OAUTH_CLIENTID = "b429386f858944f799d2a12f57e73316"
        val API_OAUTH_CLIENTSECRET = "c11a7abc3b8240f5b8ad08240e4bec60"
        val API_OAUTH_REDIRECT = "com.pierrejacquier.todoboard://todoist/"
        val API_OAUTH_SCOPE = "data:read"
        val API_OAUTH_STATE = UUID.randomUUID().toString()
    }

    @Inject
    lateinit var todoistApi: TodoistApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_setup)
        TodoboardApp.boardSetupComponent.inject(this)
        stepperLayout.adapter = SetupFragmentStepAdapter(supportFragmentManager, this)
    }

    public fun startTodoistAuth() {
        val uri = "$API_LOGIN_URL?client_id=$API_OAUTH_CLIENTID&scope=$API_OAUTH_SCOPE&state=$API_OAUTH_STATE"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()

        val uri = intent.data
        if (uri != null && uri.toString().startsWith(API_OAUTH_REDIRECT)) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                // TODO We can probably do something with this code! Show the user that we are logging them in

                val prefs = this.getSharedPreferences(
                        BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

                val call = todoistApi.getAccessToken(API_OAUTH_CLIENTID, API_OAUTH_CLIENTSECRET, code)
                call.enqueue(object : Callback<AccessToken> {
                    override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                        val statusCode = response.code()
                        if (statusCode == 200) {
                            val token = response.body()
                            prefs.edit().putBoolean("oauth.loggedin", true).apply()
                            prefs.edit().putString("oauth.accesstoken", token?.access_token).apply()
//                            prefs.edit().putString("oauth.refreshtoken", token.getRefreshToken()).apply()
                            prefs.edit().putString("oauth.tokentype", token?.token_type).apply()

                            Log.e("LOG", "FUCKIN LOGGED")
                            // TODO Show the user they are logged in
                        } else {
                            // TODO Handle errors on a failed response
                            Log.e("LOG", "FUCKIN failed")
                            Log.e("LOG", statusCode.toString())
                            Log.e("LOG", response.errorBody()?.string())
                        }
                    }

                    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                        // TODO Handle failure
                        Log.e("LOG", "FUCKIN failure")
                    }
                })
            } else {
                // TODO Handle a missing code in the redirect URI
                Log.e("LOG", "FUCKIN code")
            }
        }
    }
}
