package com.pierrejacquier.todoboard.screens.main.fragments.boards

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierrejacquier.todoboard.*
import com.pierrejacquier.todoboard.screens.details.getDetailsIntent
import com.pierrejacquier.todoboard.commons.RxBaseFragment
import com.pierrejacquier.todoboard.commons.extensions.inflate
import com.pierrejacquier.todoboard.commons.extensions.dp
import com.pierrejacquier.todoboard.commons.extensions.log
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.BoardExtendedWithProjects
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_fragment_boards.*
import javax.inject.Inject
import com.pierrejacquier.todoboard.screens.board.getBoardIntent
import com.pierrejacquier.todoboard.screens.main.MainActivity
import com.pierrejacquier.todoboard.screens.main.adapters.BoardsAdapter
import com.pierrejacquier.todoboard.screens.main.fragments.boards.di.DaggerBoardsListFragmentComponent
import com.pierrejacquier.todoboard.screens.setup.getSetupIntent
import e
import io.reactivex.Observable
import java.util.*
import kotlin.collections.ArrayList


class BoardsListFragment : RxBaseFragment() {

    companion object {
        private const val KEY_BOARDS = "myBoards"
    }

    @Inject
    lateinit var database: AppDatabase

    lateinit var boardsAdapter: BoardsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerBoardsListFragmentComponent.builder()
                .todoboardAppComponent(TodoboardApp.withFragment(this).component)
                .build()
                .inject(this)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.main_fragment_boards)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newBoardButton.setOnClickListener { addBoard() }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, ItemTouchHelper.DOWN){
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                if (viewHolder != null && target != null) {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition

                    with (boardsRV.adapter as BoardsAdapter) {
                        val prev = items.removeAt(fromPosition)
                        items.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, prev)
                        notifyItemMoved(fromPosition, toPosition)
                    }
                    setNewBoardsOrdering()
                }
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {}
        })

        boardsAdapter = BoardsAdapter()
        boardsAdapter.onConfigureClick = { board -> activity?.startActivity(activity?.getDetailsIntent(board)) }
        boardsAdapter.onLaunchClick = { board -> activity?.startActivity(activity?.getBoardIntent(board)) }

        boardsRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = boardsAdapter
            (itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                        with(activity as MainActivity) {
                            supportActionBar?.elevation = (if (dy == 0) 0 else 4).dp(context).toFloat()
                    }
                }
            })

            itemTouchHelper.attachToRecyclerView(this)
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_BOARDS)) {
            boardsRV?.let {
                boardsAdapter.items = savedInstanceState.getParcelableArrayList(KEY_BOARDS)
                updateBoardsLayout(boardsAdapter.items)
            }
        } else {
            requestBoards()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        boardsRV?.let {
            if (boardsAdapter.items.isNotEmpty()) {
                outState.putParcelableArrayList(KEY_BOARDS, ArrayList(boardsAdapter.items))
            }
        }
    }

    private fun updateBoardsLayout(boards: List<BoardExtendedWithProjects>) {
        if (boards.isEmpty()) {
            noBoardsMessage.visibility = View.VISIBLE
            boardsRV.visibility = View.GONE
        } else {
            noBoardsMessage.visibility = View.GONE
            boardsRV.visibility = View.VISIBLE
        }
    }

    private fun requestBoards() {
        val subscription =  database.boardsDao().getBoardsExtendedWithProjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ retrievedBoards ->
                    boardsRV?.let {
                        boardsAdapter.items = ArrayList(
                                retrievedBoards.filter { it.board?.userId != 0.toLong() }
                                        .sortedBy { it.board?.order }
                        )
                        updateBoardsLayout(retrievedBoards)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                            (activity as MainActivity).updateAppShortcuts(boardsAdapter.items.map { it.board!! }.take(5))
                        }
                    }
                }, { err -> e { err.message?: "" } })
        subscriptions.add(subscription)

    }

    private fun addBoard() {
        activity?.startActivity(activity?.getSetupIntent())
    }

    private fun setNewBoardsOrdering() {
        Handler().postDelayed({
            for ((counter, item) in boardsAdapter.items.withIndex()) {
                item.board?.let {
                    it.order = counter + 1
                    updateBoard(it)
                }
            }
        }, 500)
    }

    private fun updateBoard(board: Board) {
        val boardSub = Observable.fromCallable { database.boardsDao().updateBoard(board) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        subscriptions.add(boardSub)
    }

}