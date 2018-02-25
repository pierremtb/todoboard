package com.pierrejacquier.todoboard.screens.board

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.TodoboardApp
import com.pierrejacquier.todoboard.commons.RxBaseActivity
import com.pierrejacquier.todoboard.commons.extensions.dp
import com.pierrejacquier.todoboard.commons.extensions.log
import com.pierrejacquier.todoboard.data.api.SyncService
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.data.model.todoist.User
import com.pierrejacquier.todoboard.screens.board.di.BoardActivityComponent
import com.pierrejacquier.todoboard.screens.board.di.DaggerBoardActivityComponent
import com.pierrejacquier.todoboard.screens.board.fragments.block.ItemsBlockFragment
import com.pierrejacquier.todoboard.screens.board.fragments.header.HeaderFragment
import com.pierrejacquier.todoboard.screens.board.fragments.project.ProjectBlockFragment
import com.pierrejacquier.todoboard.screens.details.getDetailsIntent
import com.pierrejacquier.todoboard.screens.main.MainActivity
import e
import i
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.board_activity.*
import kotlinx.android.synthetic.main.main_activity.*
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.Crashlytics
import java.util.concurrent.TimeUnit


fun Context.getBoardIntent(board: Board?): Intent {
    if (board == null) {
        return Intent(this, MainActivity::class.java)
    }
    return Intent(this, BoardActivity::class.java).apply {
        putExtra(BOARD_KEY, board.id)

    }
}

const val BOARD_KEY = "board"

class BoardActivity : RxBaseActivity() {

    companion object {
        const val AUTO_REFRESH_SECONDS = 20

        const val OVERDUE_FRAGMENT = "overdue"
        const val TODAY_FRAGMENT = "today"
        const val TOMORROW_FRAGMENT = "tomorrow"
        const val LATER_FRAGMENT = "later"
        const val UNDATED_FRAGMENT = "undated"

        const val ITEM_HEIGHT_SUP = 10
        const val TITLE_HEIGHT_SUP = 8
        const val SPACING_HEIGHT = 2 * 8

        const val MINIMUM_DISPLAYED_ITEMS = 2
    }

    private val context = this

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var syncService: SyncService

    @Inject
    lateinit var itemsManager: ItemsManager

    var boardId: Long = 0

    lateinit var board: Board
    var projects: List<Project> = emptyList()

    lateinit var component: BoardActivityComponent

    var user: User? = null

    private val blocks = ArrayList<Block>()

    private lateinit var refreshTimer: Timer

    private var availableHeight = 0

    private var columns: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = DaggerBoardActivityComponent.builder()
                .todoboardAppComponent(TodoboardApp.withActivity(this).component)
                .build()
        component.inject(this)


        setContentView(R.layout.board_activity)
        setSupportActionBar(hideableToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        headerLayout.setOnClickListener {
            showSystemUI()
            if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_PC)) {
                popToolbar()
            }
        }

        window.decorView.setOnSystemUiVisibilityChangeListener {
            if (it == View.SYSTEM_UI_FLAG_VISIBLE) {
                popToolbar()
            }
        }

        hideSystemUI()
        supportActionBar?.hide()

        boardId = intent.getLongExtra(BOARD_KEY, 0)

        if (boardId == 0.toLong()) {
            finish()
            return
        }

        requestData()

        columns = getColumnsCount()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_board, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> { finish() }
            R.id.action_configure -> {
                startActivity(getDetailsIntent(board))
                finish()
            }
            R.id.action_cast -> {
                MaterialDialog.Builder(this)
                        .title(R.string.cast_not_available_yet)
                        .content(R.string.but_definitely_on_the_road_map)
                        .positiveText(R.string.okay)
                        .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        columns = getColumnsCount()
        if (this::board.isInitialized) {
            if (!board.projectViewEnabled) {
                Handler().postDelayed({
                    resetHeights()
                    checkHeights()
                }, 500)
            } else {
                showProjectBlock()
            }
        }
    }

    private fun getColumnsCount(): Int {
        if (!this::board.isInitialized) {
            return columns
        }

        if (!board.allowForMultiColumns) {
            return columns
        }
        return resources.getInteger(R.integer.board_activity_columns_count)
    }

    override fun onDestroy() {
        if (this::refreshTimer.isInitialized) {
            refreshTimer.cancel()
        }
        super.onDestroy()
    }

    private fun popToolbar() {
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_PC)) {
            hideableToolbar.setPadding(0, 0,0, 0)
            hideableToolbar.layoutParams.height = 56.dp(context)
        } else {
            hideableToolbar.setPadding(0, 24.dp(context),0, 0)
            hideableToolbar.layoutParams.height = 80.dp(context)
        }
        supportActionBar?.show()
        Handler().postDelayed( {
            hideSystemUI()
            supportActionBar?.hide()
        }, 2200)
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN

                or View.SYSTEM_UI_FLAG_IMMERSIVE)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_VISIBLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun showBlocksFragments() {
        for (block in blocks) {
            with (block) {
                val itemsSub = itemsManager.getItemsObservable(type)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            itemsCount = it.size
                            resetHeights()
                            checkHeights()
                        }
                subscriptions.add(itemsSub)
                showItemsFragment(type, layout, key)
            }
        }
    }

    private fun resetHeights() {
        availableHeight = blocksWrapper.measuredHeight
        for (block in blocks) {
            with (block) {
                if (itemsCount > 0) {
                    val actualCount = Math.ceil(itemsCount.toDouble() / getColumnsCount()).toInt()
                    height = (board.fontSize + TITLE_HEIGHT_SUP + SPACING_HEIGHT + actualCount * (board.fontSize + ITEM_HEIGHT_SUP)).dp(context)
                    val frame = findViewById<FrameLayout>(layout)
                    frame.layoutParams.height = height
                    frame.visibility = View.VISIBLE
                } else {
                    height = 0
                }
            }
        }
    }

    private fun areHeightsGood() = blocks.sumBy { it.height } <= availableHeight

    private fun checkHeights() {
        val itemHeight = ITEM_HEIGHT_SUP + board.fontSize
        val displayedItemsCount = if (getColumnsCount() > 1) 1 else MINIMUM_DISPLAYED_ITEMS
        val minimumHeight = (displayedItemsCount * itemHeight + TITLE_HEIGHT_SUP + board.fontSize + SPACING_HEIGHT).dp(context)

        // Checking if the minimum size is still too much or already good
        if (blocks.filter { it.height != 0}.count() * minimumHeight > availableHeight || areHeightsGood()) {
            return
        }

        for (i in (blocks.size - 1) downTo 0) {
            if (areHeightsGood()) {
                break
            }
            if (blocks[i].height > minimumHeight + itemHeight) {
                while (!areHeightsGood() && blocks[i].height >= minimumHeight + itemHeight) {
                    blocks[i].height -= itemHeight
                    findViewById<FrameLayout>(blocks[i].layout).layoutParams.height = blocks[i].height
                }
            }
        }
    }

    private fun showHeaderFragment() {
        val ft = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString(HeaderFragment.KEY_USER, user?.fullName)
        val fragment = HeaderFragment()
        fragment.arguments = bundle
        ft.replace(R.id.headerLayout, fragment)
        ft.commit()
    }

    private fun showItemsFragment(type: Int, layout: Int, tag: String) {
        val ft = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        with (bundle) {
            putInt(ItemsBlockFragment.KEY_TYPE, type)
            putInt(ItemsBlockFragment.KEY_FONT_SIZE, board.fontSize)
            putBoolean(ItemsBlockFragment.KEY_MULTI_COLUMNS, board.allowForMultiColumns)
            putBoolean(ItemsBlockFragment.KEY_ALLOW_FOR_AUTO_SCROLL, board.allowForAutoScroll)
            putInt(ItemsBlockFragment.KEY_AUTO_SCROLL_DELAY, board.autoScrollDelay)
        }
        val fragment = ItemsBlockFragment()
        fragment.arguments = bundle
        fragment.retainInstance = false
        ft.replace(layout, fragment, tag)
        ft.commit()
    }

    private fun showProjectBlock() {
        if (board.projectViewEnabled && projects.getOrNull(0) != null) {
            projectItemsLayout.visibility = View.VISIBLE
            val ft = supportFragmentManager.beginTransaction()
            val bundle = Bundle()
            with (bundle) {
                putInt(ItemsBlockFragment.KEY_FONT_SIZE, board.fontSize)
                putParcelable(ProjectBlockFragment.KEY_PROJECT_ID, projects[0])
                putBoolean(ItemsBlockFragment.KEY_ALLOW_FOR_AUTO_SCROLL, board.allowForAutoScroll)
                putInt(ItemsBlockFragment.KEY_AUTO_SCROLL_DELAY, board.autoScrollDelay)
            }
            val fragment = ProjectBlockFragment()
            fragment.arguments = bundle
            ft.replace(R.id.projectItemsLayout, fragment, "project-view")
            ft.commit()
        }
    }

    private fun enableBlocks() {
        with (board) {
            if (!projectViewEnabled) {
                if (overdueEnabled)
                    blocks.add(Block(OVERDUE_FRAGMENT, R.id.overdueItemsLayout, ItemsBlockFragment.OVERDUE, 0))
                if (todayEnabled)
                    blocks.add(Block(TODAY_FRAGMENT, R.id.todayItemsLayout, ItemsBlockFragment.TODAY, 0))
                if (tomorrowEnabled)
                    blocks.add(Block(TOMORROW_FRAGMENT, R.id.tomorrowItemsLayout, ItemsBlockFragment.TOMORROW, 0))
                if (laterEnabled)
                    blocks.add(Block(LATER_FRAGMENT, R.id.laterItemsLayout, ItemsBlockFragment.LATER, 0))
                if (undatedEnabled)
                    blocks.add(Block(UNDATED_FRAGMENT, R.id.undatedItemsLayout, ItemsBlockFragment.UNDATED, 0))
            }
        }
    }

    private fun startSyncTimer() {
        refreshTimer = fixedRateTimer("sync-timer", initialDelay = 0, period = (1000 * AUTO_REFRESH_SECONDS).toLong()) {
            val syncSub = syncService.sync(board)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { newBoard -> board = newBoard }

            subscriptions.add(syncSub)
        }
    }

    private fun retrieveTasks() {
        val itemsSub = database.itemsDao()
                .getBoardToDoItemsFromProjects(
                        projects.map { it.id }.toTypedArray()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ itemsManager.items = it }, { err -> e { err.message ?: "" } })
        subscriptions.add(itemsSub)
    }

    private fun requestData() {
        val projectsSub = database.boardsDao().findBoardExtendedWithProjects(boardId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .take(1)
                .subscribe({ boardExt ->
                    if (boardExt.board == null) {
                        finish()
                        return@subscribe
                    }
                    board = boardExt.board!!

                    enableBlocks()
                    startSyncTimer()

                    projects = boardExt.projectsJoins.map { it.project[0] }
                    user = boardExt.user.getOrNull(0)

                    showProjectBlock()
                    showHeaderFragment()
                    showBlocksFragments()

                    retrieveTasks()
                }, { err -> e { err.message ?: "" } })
        subscriptions.add(projectsSub)    }
}
