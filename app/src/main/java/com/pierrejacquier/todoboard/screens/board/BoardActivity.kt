package com.pierrejacquier.todoboard.screens.board

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
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
import com.pierrejacquier.todoboard.screens.main.MainActivity
import e
import i
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.board_activity.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

fun Context.getBoardIntent(board: Board?): Intent {
    if (board == null) {
        return Intent(this, MainActivity::class.java)
    }
    val bundle = Bundle()
    bundle.putParcelable(BOARD_KEY, board)
    return Intent(this, BoardActivity::class.java).apply {
        putExtras(bundle)
    }
}

private const val BOARD_KEY = "board"

class BoardActivity : RxBaseActivity() {

    companion object {
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        private val OVERDUE_FRAGMENT = "overdue"
        private val TODAY_FRAGMENT = "today"
        private val TOMORROW_FRAGMENT = "tomorrow"
        private val LATER_FRAGMENT = "later"
        private val UNDATED_FRAGMENT = "undated"

        private val ITEM_HEIGHT = 28
        private val TITLE_HEIGHT = 28
        private val SPACING_HEIGHT = 2 * 8

        private val MINIMUM_DISPLAYED_ITEMS = 2
    }

    private val context = this

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var syncService: SyncService

    @Inject
    lateinit var itemsManager: ItemsManager

    lateinit var board: Board
    var projects: List<Project> = emptyList()

    lateinit var component: BoardActivityComponent

    var user: User? = null

    private val blocks = ArrayList<Block>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        component = DaggerBoardActivityComponent.builder()
                .todoboardAppComponent(TodoboardApp.withActivity(this).component)
                .build()
        component.inject(this)

        setContentView(R.layout.board_activity)
        setSupportActionBar(hideableToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        board = intent.extras.getParcelable(BOARD_KEY)

        with (board) {
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

        fixedRateTimer("sync-timer", initialDelay = 0, period = 1000 * 20) {
            "OUHLALALALLAA".log()
            val syncSub = syncService.sync(board)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { newBoard -> board = newBoard }

            subscriptions.add(syncSub)
        }

        requestData()

        val sub = itemsManager.sizesObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it.toString() }
        subscriptions.add(sub)

        hideSystemUI()
        supportActionBar?.hide()

        window.decorView.setOnSystemUiVisibilityChangeListener {
            it.log()
            if (it == View.SYSTEM_UI_FLAG_VISIBLE) {
                supportActionBar?.show()
                Handler().postDelayed( {
                    hideSystemUI()
                    supportActionBar?.hide()
                }, 2200)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> { finish() }
        }

        return super.onOptionsItemSelected(item)
    }


    // This snippet hides the system bars.
    private fun hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar

                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar

                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
//    private fun showSystemUI() {
//        mDecorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//    }


    private fun showSections() {
        showHeaderFragment()
        for (block in blocks) {
            with (block) {
                val itemsSub = itemsManager.getItemsObservable(type)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { blockItems ->
                            itemsCount = blockItems.size
                            height = (TITLE_HEIGHT + SPACING_HEIGHT + blockItems.size * ITEM_HEIGHT).dp(context)
                            val frame = findViewById<FrameLayout>(layout)
                            frame.layoutParams.height = height
                            frame.visibility = View.VISIBLE
                            checkHeights()
                        }
                subscriptions.add(itemsSub)
                showItemsFragment(type, layout, key)
            }
        }
    }

    private fun checkHeights() {
        val minimumHeight = (MINIMUM_DISPLAYED_ITEMS * ITEM_HEIGHT + TITLE_HEIGHT + SPACING_HEIGHT).dp(context)
        val blocksTotalHeight = blocks.sumBy { it.height }
        if (blocksTotalHeight <= blocksWrapper.measuredHeight) {
            return
        }
        for (i in (blocks.size - 1) downTo 0) {
            with (blocks[i]) {
                if (height > minimumHeight) {
                    height = minimumHeight
                    findViewById<FrameLayout>(layout).layoutParams.height = height
                    checkHeights()
                    return
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
        bundle.putInt(ItemsBlockFragment.KEY_TYPE, type)
        val fragment = ItemsBlockFragment()
        fragment.arguments = bundle
        ft.replace(layout, fragment, tag)
        ft.commit()
    }

    private fun requestData() {
        val projectsSub = database.boardsDao().findBoardExtendedWithProjects(board.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ boardExt ->
                    projects = boardExt.projectsJoins.map { it.project[0] }
                    user = boardExt.user.getOrNull(0)
                    showSections()
                    val itemsSub = database.itemsDao()
                            .getBoardToDoItemsFromProjects(
                                    projects.map { it.id }.toTypedArray()
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ itemsManager.items = it }, { err -> e { err.message ?: "" } })
                    subscriptions.add(itemsSub)
                }, { err -> e { err.message ?: "" } })
        subscriptions.add(projectsSub)
    }
}
