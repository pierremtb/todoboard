package com.pierrejacquier.todoboard.screens.main.fragments.boards

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import java.util.*


class BoardsListFragment : RxBaseFragment() {

    companion object {
        private val KEY_BOARDS = "myBoards"
    }

    @Inject
    lateinit var database: AppDatabase


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

        boardsRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = BoardsAdapter()

            with (adapter as BoardsAdapter) {
                onConfigureClick = { board ->
                    activity?.startActivity(activity?.getDetailsIntent(board))
                }
                onLaunchClick = { board ->
                activity?.startActivity(activity?.getBoardIntent(board))
                }
            }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                        with(activity as MainActivity) {
                            supportActionBar?.elevation = (if (dy == 0) 0 else 4).dp(context).toFloat()
                    }
                }
            })
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_BOARDS)) {
            boardsRV?.let {
                with(it.adapter as BoardsAdapter) {
                    items = savedInstanceState.getParcelableArrayList(KEY_BOARDS)
                    updateBoardsLayout(items)
                }
            }
        } else {
            requestBoards()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        boardsRV?.let {
            with(it.adapter as BoardsAdapter) {
                if (items.isNotEmpty()) {
                    outState.putParcelableArrayList(KEY_BOARDS, ArrayList(items))
                }
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
                        with(boardsRV.adapter as BoardsAdapter) {
                            items = retrievedBoards.filter { it.board?.userId != 0.toLong() }
                        }
                        updateBoardsLayout(retrievedBoards)
                    }
                }, { err -> e { err.message?: "" } })
        subscriptions.add(subscription)

    }

    private fun addBoard() {
        activity?.startActivity(activity?.getSetupIntent())
    }

}