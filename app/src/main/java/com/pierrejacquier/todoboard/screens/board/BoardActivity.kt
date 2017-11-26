package com.pierrejacquier.todoboard.screens.board

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.TodoboardApp
import com.pierrejacquier.todoboard.commons.RxBaseActivity
import com.pierrejacquier.todoboard.data.api.SyncService
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.screens.board.di.BoardActivityComponent
import com.pierrejacquier.todoboard.screens.board.di.DaggerBoardActivityComponent
import com.pierrejacquier.todoboard.screens.board.fragments.block.ItemsBlockFragment
import com.pierrejacquier.todoboard.screens.board.fragments.header.HeaderFragment
import com.pierrejacquier.todoboard.screens.main.MainActivity
import e
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.board_activity.*
import javax.inject.Inject

fun Context.BoardIntent(board: Board?): Intent {
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
        private val AUTO_HIDE = true
        private val AUTO_HIDE_DELAY_MILLIS = 3000
        private val UI_ANIMATION_DELAY = 300

        private val TODAY_FRAGMENT = "today"
        private val TOMORROW_FRAGMENT = "tomorrow"
        private val LATER_FRAGMENT = "later"
        private val UNDATED_FRAGMENT = "undated"
    }

    private val hideHandler = Handler()
    private val hidePart2Runnable = Runnable {
//        fullscreen_content.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LOW_PROFILE or
//                        View.SYSTEM_UI_FLAG_FULLSCREEN or
//                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val showPart2Runnable = Runnable {
        supportActionBar?.show()
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }

    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var syncService: SyncService

    @Inject
    lateinit var itemsManager: ItemsManager

    lateinit var board: Board
    var projects: List<Project> = emptyList()

    lateinit var component: BoardActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        component = DaggerBoardActivityComponent.builder()
                .todoboardAppComponent(TodoboardApp.withActivity(this).component)
                .build()
        component.inject(this)

        setContentView(R.layout.board_activity)
        setSupportActionBar(hideableToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

//        fullscreen_content.setOnClickListener { toggle() }
        board = intent.extras.getParcelable(BOARD_KEY)

        showHeaderFragment()
        showItemsFragment(ItemsBlockFragment.TODAY, R.id.firstItemsLayout, TODAY_FRAGMENT)
        showItemsFragment(ItemsBlockFragment.TOMORROW, R.id.secondItemsLayout, TOMORROW_FRAGMENT)
        showItemsFragment(ItemsBlockFragment.UNDATED, R.id.thirdItemsLayout, UNDATED_FRAGMENT)

        val syncSub = syncService.sync(board)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { newBoard -> board = newBoard }

        subscriptions.add(syncSub)

        requestData()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delayedHide(100)
    }

    private fun showHeaderFragment() {
        val ft = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString(HeaderFragment.KEY_USER,board.userName)
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

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        supportActionBar?.hide()
        mVisible = false

//        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
//        fullscreen_content.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true
        supportActionBar?.show()

        hideHandler.removeCallbacks(hidePart2Runnable)
//        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(mHideRunnable)
        hideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    private fun requestData() {
        val projectsSub = database.projectsDao().findBoardProjects(board.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ retrievedProjects ->
                    projects = retrievedProjects
                    val itemsSub = database.itemsDao()
                            .getBoardToDoItemsFromProjects(
                                    board.id,
                                    projects.filter { it.selected }.map { it.id }.toTypedArray()
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                e { "//////// BOARD items subscsribe" }
                                e { it.map { it.content }.toString() }
                                e { "//////// END" }
                                itemsManager.items = it
                            }, { err -> e { err.message ?: "" } })
                    subscriptions.add(itemsSub)
                }, { err -> e { err.message ?: "" } })
        subscriptions.add(projectsSub)
    }
}
