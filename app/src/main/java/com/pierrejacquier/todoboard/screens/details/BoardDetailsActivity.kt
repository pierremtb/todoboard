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
import android.widget.CompoundButton
import android.widget.NumberPicker
import android.widget.Switch
import com.afollestad.materialdialogs.MaterialDialog
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.TodoboardApp
import com.pierrejacquier.todoboard.commons.CircleTransform
import com.pierrejacquier.todoboard.commons.RxBaseActivity
import com.pierrejacquier.todoboard.commons.extensions.log
import com.pierrejacquier.todoboard.commons.extensions.snack
import com.pierrejacquier.todoboard.data.api.SyncService
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.BoardProjectJoin
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.data.model.todoist.User
import com.pierrejacquier.todoboard.databinding.DetailsActivityBinding
import com.pierrejacquier.todoboard.screens.board.getBoardIntent
import com.pierrejacquier.todoboard.screens.details.adapters.SelectableProjectsAdapter
import com.pierrejacquier.todoboard.screens.details.di.DaggerBoardDetailsActivityComponent
import com.pierrejacquier.todoboard.screens.main.MainActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.details_activity.*
import javax.inject.Inject
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates


fun Context.getDetailsIntent(board: Board?): Intent {
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

    companion object {
        const val MIN_FONT_SIZE = 8
        const val MAX_FONT_SIZE = 26
        const val DEFAULT_FONT_SIZE = 18
    }

    private lateinit var board: Board
    lateinit var user: User
    private var projectsJoins: List<BoardProjectJoin> by Delegates.observable(emptyList()) { _, old, new ->
        updateBoardProjects(old, new)
    }

    private var selectableProjects: List<Project>
        get() = with(selectableProjectsRV.adapter as SelectableProjectsAdapter) { return items }
        set(newItems) {
            with(selectableProjectsRV.adapter as SelectableProjectsAdapter) { items = newItems }
        }

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var picasso: Picasso

    @Inject
    lateinit var syncService: SyncService

    lateinit var binding: DetailsActivityBinding

    private lateinit var projectsDialog: MaterialDialog

    private lateinit var selectableProjectsRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.details_activity)

        setSupportActionBar(boardToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        board = intent.extras.getParcelable(BOARD_DETAILS_KEY)

        binding.board = board

        binding.projectViewSwitch.setOnClickListener {
            if (binding.projectViewSwitch.isChecked && projectsJoins.size > 1) {
                board.projectViewEnabled = false
                binding.root.snack(resources.getString(R.string.you_cant_have_more_than_one_project_for_that)) {}
            }
            binding.invalidateAll()
        }

        DaggerBoardDetailsActivityComponent.builder()
                .todoboardAppComponent(TodoboardApp.withActivity(this).component)
                .build()
                .inject(this)

        loadAdditionalData()

        launchBoardFab.setOnClickListener { _ ->
            updateBoard()
            startActivity(getBoardIntent(board))
        }


        projectsDialog = MaterialDialog.Builder(this)
                .title(R.string.manage_selected_projects)
                .customView(R.layout.details_manage_projects, true)
                .positiveText(R.string.done)
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .onPositive { dialog, _ ->
                    val newProjectsJoins = ArrayList<BoardProjectJoin>()
                    selectableProjects.forEach {
                        if (it.selected && it.id != null) {
                            newProjectsJoins.add(BoardProjectJoin(id = 0, boardId = board.id, projectId = it.id!!))
                        }
                    }
                    projectsJoins = newProjectsJoins
                    updateProjects()
                    dialog.hide()
                }
                .build()

        selectableProjectsRV = projectsDialog.customView as RecyclerView

        selectableProjectsRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = SelectableProjectsAdapter()
        }

        val context = this

        manageProjectsButton.setOnClickListener { projectsDialog.show() }

        fontSizeRow.setOnClickListener {
            val dialog = MaterialDialog.Builder(context)
                    .title(R.string.font_size)
                    .customView(R.layout.dialog_font_size_picker, false)
                    .positiveText(R.string.done)
                    .build()
            val numberPicker = dialog.customView?.findViewById<NumberPicker>(R.id.numberPicker)
            numberPicker?.let {
                it.minValue = MIN_FONT_SIZE
                it.maxValue = MAX_FONT_SIZE
                it.value = board.fontSize
            }
            numberPicker?.setOnValueChangedListener { _, _, newSize ->
                board.fontSize = newSize
            }
            dialog.setOnDismissListener { binding.invalidateAll() }
            dialog.show()
        }

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
        val userSub = database.boardsDao().findBoardExtended(board.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    user = it.user[0]
                    projectsJoins = it.projectsJoins
                    loadProjects()
                }, {})
        subscriptions.add(userSub)
    }

    private fun loadProjects() {
        val projectsSub = database.projectsDao().findUserProjects(user.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val projectsIds = projectsJoins.map { it.projectId }
                    selectableProjects = it.map { it.selected = projectsIds.contains(it.id); it }.sortedBy { it.itemOrder }
                    updateProjects()
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
        val projectsIds = projectsJoins.map { it.projectId }
        binding.selectedProjects = selectableProjects.filter {projectsIds.contains(it.id) }
        showAdditionalData()
    }

    private fun updateBoard() {
        val boardSub = Observable.fromCallable { database.boardsDao().updateBoard(board) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        subscriptions.add(boardSub)
    }

    private fun updateBoardProjects(old: List<BoardProjectJoin>, new: List<BoardProjectJoin>) {
        val boardSub = Observable.fromCallable { database.boardProjectJoinsDao().updateProjectsJoinsOfBoard(old, new) }
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
                    loadAdditionalData()
                    dialog.hide()
                }, {
                    binding.root.snack(R.string.error) {}
                    dialog.hide()
                })
        subscriptions.add(syncSub)
    }
}