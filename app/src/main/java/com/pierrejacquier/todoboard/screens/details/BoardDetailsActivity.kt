package com.pierrejacquier.todoboard.screens.details

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.TodoboardApp
import com.pierrejacquier.todoboard.commons.CircleTransform
import com.pierrejacquier.todoboard.commons.RxBaseActivity
import com.pierrejacquier.todoboard.commons.extensions.snack
import com.pierrejacquier.todoboard.data.api.SyncService
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.data.model.todoist.User
import com.pierrejacquier.todoboard.databinding.ActivityBoardDetailsBinding
import com.pierrejacquier.todoboard.screens.board.BoardIntent
import com.pierrejacquier.todoboard.screens.details.adapters.SelectableProjectsAdapter
import com.pierrejacquier.todoboard.screens.details.adapters.SelectedProjectsAdapter
import com.pierrejacquier.todoboard.screens.details.di.DaggerBoardDetailsActivityComponent
import com.pierrejacquier.todoboard.screens.main.MainActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_board_details.*
import javax.inject.Inject
import com.squareup.picasso.Picasso
import e


fun Context.BoardDetailsIntent(board: Board?): Intent {
    if (board == null) {
        return Intent(this, MainActivity::class.java)
    }
    val bundle = Bundle()
    bundle.putParcelable(BOARD_DETAILS_KEY, board)
    return Intent(this, BoardDetailsActivity::class.java).apply {
        putExtras(bundle)
    }
}

private const val BOARD_DETAILS_KEY = "board-details"

class BoardDetailsActivity : RxBaseActivity() {

    lateinit private var board: Board
    lateinit var user: User

    var selectedProjects: List<Project>
        get() = with(selectedProjectsRV.adapter as SelectedProjectsAdapter) { return items }
        set(newItems) {
            with(selectedProjectsRV.adapter as SelectedProjectsAdapter) { items = newItems }
        }

    var selectableProjects: List<Project>
        get() = with(selectableProjectsRV.adapter as SelectableProjectsAdapter) { return items }
        set(newItems) {
            with(selectableProjectsRV.adapter as SelectableProjectsAdapter) { items = newItems }
        }

    private var projects = ArrayList<Project>()

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var picasso: Picasso

    @Inject
    lateinit var syncService: SyncService

    lateinit var binding: ActivityBoardDetailsBinding

    lateinit private var projectsDialog: MaterialDialog

    lateinit private var selectableProjectsRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_details)

        setSupportActionBar(boardToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        board = intent.extras.getParcelable(BOARD_DETAILS_KEY)
        binding.board = board

        DaggerBoardDetailsActivityComponent.builder()
                .todoboardAppComponent(TodoboardApp.withActivity(this).component)
                .build()
                .inject(this)

        loadAdditionalData()

        launchBoardFab.setOnClickListener { _ -> startActivity(BoardIntent(board)) }


        selectedProjectsRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = SelectedProjectsAdapter()
        }

        projectsDialog = MaterialDialog.Builder(this)
                .title(R.string.manage_selected_projects)
                .customView(R.layout.manage_projects, true)
                .positiveText(R.string.done)
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .onPositive { dialog, _ ->
                    selectedProjects = selectableProjects.filter { it.selected }
                    dialog.hide()
                    updateProjects()
                }
                .build()

        selectableProjectsRV = projectsDialog.customView as RecyclerView

        selectableProjectsRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = SelectableProjectsAdapter()
        }

        manageProjectsButton.setOnClickListener { projectsDialog.show() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_board_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            R.id.action_delete -> showDeleteConfirm()
            R.id.action_force_sync -> forceSync()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        updateBoard()
        super.onDestroy()
    }

    private fun showDeleteConfirm() {
        MaterialDialog.Builder(this)
                .title(R.string.do_you_want_to_delete_this_board)
                .content(R.string.this_is_irreversible)
                .positiveText(R.string.proceed)
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .onPositive { _, _ ->  deleteBoard() }
                .negativeText(R.string.go_back)
                .negativeColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
                .onNegative { dialog, _ -> dialog.hide() }
                .show()
    }

    private fun deleteBoard() {
        val delSub = Observable.fromCallable { database.boardsDao().deleteBoard(board) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { finish() }
        subscriptions.add(delSub)
    }

    private fun loadAdditionalData() {
        detailsLayout.visibility = View.GONE
        loadUser()
    }

    private fun loadUser() {
        val userSub = database.usersDao().findUser(board.userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    user = it
                    loadProjects()
                }
        subscriptions.add(userSub)
    }

    private fun loadProjects() {
        val projectsSub = database.projectsDao().findBoardProjects(board.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { retrievedProjects ->
                    selectableProjects = retrievedProjects
                    selectedProjects = selectableProjects.filter { it.selected }
                    showAdditionalData()
                }
        subscriptions.add(projectsSub)
    }

    private fun showAdditionalData() {
        binding.user = user
        with (userListItem) {
            picasso.load(user.avatarMedium)
                    .placeholder(R.drawable.ic_account_circle)
                    .transform(CircleTransform())
                    .into(avatarView)
        }
        detailsLayout.visibility = View.VISIBLE
    }

    private fun updateProjects() {
        val projectsSub = Observable.fromCallable { database.projectsDao().updateProjects(selectableProjects) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        subscriptions.add(projectsSub)
    }

    private fun updateBoard() {
        val boardSub = Observable.fromCallable { database.boardsDao().updateBoard(board) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        subscriptions.add(boardSub)
    }

    private fun forceSync() {
        val dialog = MaterialDialog.Builder(this)
                .title(R.string.syncing)
                .content(R.string.please_wait)
                .progress(true, 0)
                .build()
        dialog.show()
        val syncSub = syncService.sync(board, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    board = it
                    dialog.hide()
                }, {
                    binding.root.snack(R.string.error) {}
                    dialog.hide()
                })
        subscriptions.add(syncSub)
    }
}