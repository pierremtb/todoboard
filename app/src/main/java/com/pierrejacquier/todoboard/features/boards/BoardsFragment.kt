package com.pierrejacquier.todoboard.features.boards

import android.databinding.ObservableArrayList
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.pierrejacquier.todoboard.*
import com.pierrejacquier.todoboard.commons.RxBaseFragment
import com.pierrejacquier.todoboard.commons.extensions.inflate
import com.pierrejacquier.todoboard.database.boards.Board
import com.pierrejacquier.todoboard.database.boards.BoardsParcel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_boards.*
import javax.inject.Inject
import com.pierrejacquier.todoboard.databinding.BoardItemBinding


class BoardsFragment : RxBaseFragment() {

    companion object {
        private val KEY_BOARDS = "myBoards"
    }

    @Inject lateinit var BoardsManager: BoardsManager
    private var boards = ObservableArrayList<Board>()

    override fun onCreate(savedInstanceState: Bundle?) {
        TodoboardApp.boardsComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_boards)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newBoardButton.setOnClickListener { addBoard() }

        boardsRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        val boardType = Type<BoardItemBinding>(R.layout.board_item)
                .onClick {
                    Log.e("ID", it.itemId.toString())
                    activity.startActivity(activity.BoardIntent(it.binding.board?.id!!))
                }

        LastAdapter(boards, BR.board)
                .map<Board>(boardType)
                .into(boardsRV)

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_BOARDS)) {
            val boards = savedInstanceState[KEY_BOARDS] as BoardsParcel
            updateBoards(boards.items)
        } else {
            requestBoards()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (boards.isNotEmpty()) {
//            outState.putParcelable(KEY_BOARDS, BoardsParcel(boards))
        }
    }

    private fun updateBoards(boards: List<Board>) {
        if (boards.isEmpty()) {
            noBoardsMessage.visibility = View.VISIBLE
            boardsRV.visibility = View.GONE
        } else {
            noBoardsMessage.visibility = View.GONE
            boardsRV.visibility = View.VISIBLE
        }
        this.boards.apply {
            clear()
            addAll(boards)
        }
    }

    private fun requestBoards() {
        val subscription = BoardsManager.getBoards()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({retrievedBoards ->
                    Log.e("BOARDS", retrievedBoards.toString())
                    updateBoards(retrievedBoards)
                }, {e -> Log.e("ERROR", e.message?: "")})
        subscriptions.add(subscription)
    }

    private fun addBoard() {
        activity.startActivity(activity.DisplaySetupIntent())
//        val subscription = Observable.fromCallable { BoardsManager.addBoard(Board(0,"Hey")) }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe()
//        subscriptions.add(subscription)
    }

}