package com.pierrejacquier.todoboard.screens.setup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.setup_activity.*
import android.view.View
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.TodoboardApp
import com.pierrejacquier.todoboard.commons.PicassoCustomLoader
import com.pierrejacquier.todoboard.commons.RxBaseActivity
import com.pierrejacquier.todoboard.data.model.todoist.TodoistAccessToken
import com.pierrejacquier.todoboard.commons.extensions.snack
import com.pierrejacquier.todoboard.data.api.SyncService
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.data.model.todoist.User
import com.pierrejacquier.todoboard.screens.details.adapters.SelectableProjectsAdapter
import com.pierrejacquier.todoboard.screens.setup.di.DaggerBoardSetupActivityComponent
import com.squareup.picasso.Picasso
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import e
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.setup_fragment_finish_step.*
import kotlinx.android.synthetic.main.setup_fragment_provider_auth_step.*
import kotlinx.android.synthetic.main.setup_fragment_provider_details_step.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject


fun Context.DisplaySetupIntent() = Intent(this, BoardSetupActivity::class.java)

class BoardSetupActivity : RxBaseActivity(), StepperLayout.StepperListener {

    companion object {
        val API_LOGIN_URL = "https://todoist.com/oauth/authorize"
        val API_OAUTH_CLIENTID = "b429386f858944f799d2a12f57e73316"
        val API_OAUTH_CLIENTSECRET = "c11a7abc3b8240f5b8ad08240e4bec60"
        val API_OAUTH_REDIRECT = "com.pierrejacquier.todoboard://todoist/"
        val API_OAUTH_SCOPE = "data:read"
        val API_OAUTH_STATE = UUID.randomUUID().toString()

        val AUTH_STEP_INDEX = 0
        val DETAILS_STEP_INDEX = 1
        val FINISH_STEP_INDEX = 2

        val NEW_BOARD_USER_KEY = "newBoardUser"
    }

    var newBoardUser: User? = null
    var newBoardSyncToken: String = "*"
    var newBoardUserId: Long = 0
    var newBoardName: String = "New Board"
    var newBoardId: Long = 0

    var selectableProjects: List<Project>
        get() = with(projectsRV.adapter as SelectableProjectsAdapter) { return items }
        set(newItems) {
            with( projectsRV.adapter as SelectableProjectsAdapter) { items = newItems }
        }

    lateinit var newBoardAccessToken: String

    @Inject
    lateinit var syncService: SyncService

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var picasso: Picasso

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup_activity)
        DaggerBoardSetupActivityComponent.builder()
                .todoboardAppComponent(TodoboardApp.withActivity(this).component)
                .build()
                .inject(this)
        stepperLayout.adapter = SetupFragmentStepAdapter(supportFragmentManager, this)
        stepperLayout.setListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(NEW_BOARD_USER_KEY)) {
            newBoardUser = savedInstanceState.getParcelable(NEW_BOARD_USER_KEY)
        }
    }

    fun startTodoistAuth() {
        val uri = "${API_LOGIN_URL}?client_id=${API_OAUTH_CLIENTID}&scope=${API_OAUTH_SCOPE}&state=${API_OAUTH_STATE}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        setAuthLoadingState(true)
        startActivity(intent)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        if (newBoardUser != null) {
            outState?.putParcelable(NEW_BOARD_USER_KEY, newBoardUser)
        }
    }

    override fun onResume() {
        super.onResume()

        if (newBoardUser == null) {
            val uri = intent.data
            if (uri != null && uri.toString().startsWith(API_OAUTH_REDIRECT)) {
                val code = uri.getQueryParameter("code")
                if (code != null) {
                    val call = syncService.todoistApi.getAccessToken(API_OAUTH_CLIENTID, API_OAUTH_CLIENTSECRET, code)
                    setAuthLoadingState(true)
                    call.enqueue(object : Callback<TodoistAccessToken> {
                        override fun onResponse(call: Call<TodoistAccessToken>, response: Response<TodoistAccessToken>) {
                            val statusCode = response.code()
                            if (statusCode == 200) {
                                val token = response.body()
                                if (token != null) {
                                    newBoardAccessToken = token.accessToken

                                    prepareNewBoard()
                                }
                            } else {
                                e { statusCode.toString() }
                                setAuthLoadingState(false)
                                stepperLayout.snack(R.string.error) {}
                            }
                        }

                        override fun onFailure(call: Call<TodoistAccessToken>, t: Throwable) {
                            setAuthLoadingState(false)
                            e { t.message.toString() }
                            stepperLayout.snack(R.string.error) {}
                        }
                    })
                } else {
                    e { "code null" }
                    setAuthLoadingState(false)
                    stepperLayout.snack(R.string.error) {}
                }
            }
        }
    }

    override fun onStepSelected(newStepPosition: Int) {
        when (newStepPosition) {
            AUTH_STEP_INDEX -> {
                if (newBoardUser != null) {
                    displayUser()
                }
            }
            DETAILS_STEP_INDEX -> { requestProjects() }
            FINISH_STEP_INDEX -> {}
        }

    }

    override fun onError(verificationError: VerificationError?) {
    }

    override fun onBackPressed() {
        showCancelConfirmDialog()
    }

    override fun onReturn() {
        showCancelConfirmDialog()
    }

    override fun onCompleted(completeButton: View?) {
        newBoardName = newBoardNameEdit.text.toString()

        val boardSub = Observable.fromCallable {
                    database.boardsDao().updateBoard(
                            Board(
                                    newBoardId,
                                    newBoardName,
                                    newBoardAccessToken,
                                    newBoardUserId,
                                    newBoardSyncToken)
                    )
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ finish() })
        subscriptions.add(boardSub)

    }

    // Auth step

    fun prepareNewBoard() {
        val boardSub = Observable.fromCallable {
                    database.boardsDao()
                            .insertBoard(Board(newBoardId, newBoardName, newBoardAccessToken))
                }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            id -> newBoardId = id
                            syncData()
                        })
        subscriptions.add(boardSub)
    }

    fun syncData() {
        val syncSub = syncService.sync(Board(0, newBoardName, newBoardAccessToken), true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ board ->
                    newBoardUserId = board.userId
                    newBoardSyncToken = board.syncToken
                    e { newBoardUserId.toString() }
                    findUser()
                })
        subscriptions.add(syncSub)
    }

    fun findUser() {
        val userSub = database.usersDao().findUser(newBoardUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ user ->
                    newBoardUser = user
                    displayUser()
                })
        subscriptions.add(userSub)
    }

    fun displayUser() {
        if (newBoardUser != null) {
            todoistAuthButton.visibility = View.GONE
            newUserName.text = newBoardUser?.fullName
            newUserEmail.text = newBoardUser?.email
            PicassoCustomLoader(picasso)
                    .loadImage(newUserAvatar, newBoardUser?.avatarMedium, newBoardUser!!.fullName)
            newUserGroup.visibility = View.VISIBLE
        }
    }

    fun setAuthLoadingState(isLoading: Boolean) {
        if (todoistAuthButton != null) {
            if (isLoading) {
                todoistAuthButton.visibility = View.GONE
                authProgressBar.visibility = View.VISIBLE
            } else {
                todoistAuthButton.visibility = View.VISIBLE
                authProgressBar.visibility = View.GONE
            }
        }
    }

    // Details step

    private fun prepareProjectsUI() {
        projectsRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = SelectableProjectsAdapter()
        }
    }

    private fun requestProjects() {
        prepareProjectsUI()
        val projectsSub = database.projectsDao().findUserProjects(newBoardUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({  ps -> e{ ps.map { it.name }.toString()}; selectableProjects = ps }, { err -> e { err.message?: "" } })
        subscriptions.add(projectsSub)
    }

    private fun showCancelConfirmDialog() {
        MaterialDialog.Builder(this)
                .title(R.string.do_you_want_to_cancel)
                .content(R.string.current_data_will_be_lost)
                .positiveText(R.string.proceed)
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .onPositive { _, _ ->  deleteBoard() }
                .negativeText(R.string.go_back)
                .negativeColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
                .onNegative { dialog, _ -> dialog.hide() }
                .show()
    }

    private fun deleteBoard() {
        if(newBoardId == 0.toLong()) {
            finish()
            return
        }
        val boardSub = Observable.fromCallable {
            database.boardsDao().deleteBoard(
                    Board(
                            newBoardId,
                            newBoardName,
                            newBoardAccessToken,
                            newBoardUserId,
                            newBoardSyncToken)
            )
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ finish() })
        subscriptions.add(boardSub)
    }
}
