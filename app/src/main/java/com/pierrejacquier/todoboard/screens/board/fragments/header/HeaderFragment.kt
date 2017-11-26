package com.pierrejacquier.todoboard.screens.board.fragments.header

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.TodoboardApp
import com.pierrejacquier.todoboard.commons.RxBaseFragment
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.todoist.User
import com.pierrejacquier.todoboard.databinding.BoardFragmentHeaderBinding
import com.pierrejacquier.todoboard.screens.board.BoardIntent
import com.pierrejacquier.todoboard.screens.board.fragments.header.di.DaggerHeaderFragmentComponent
import com.pierrejacquier.todoboard.screens.main.adapters.BoardsAdapter
import e
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class HeaderFragment : RxBaseFragment() {

    companion object {
        val KEY_USER = "user"
    }

    @Inject
    lateinit var database: AppDatabase

    lateinit var binding: BoardFragmentHeaderBinding
    lateinit var userName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerHeaderFragmentComponent.builder()
                .todoboardAppComponent(TodoboardApp.withFragment(this).component)
                .build()
                .inject(this)

        arguments?.let {
            userName = it.getString(KEY_USER) ?: ""
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BoardFragmentHeaderBinding.inflate(inflater, container, false)
        binding.userName = userName
        binding.date = SimpleDateFormat("EEE, MMM d", Locale.US).format(Date())
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        boardsRV?.let {
//            with(it.adapter as BoardsAdapter) {
//                if (items.isNotEmpty()) {
//                    outState.putParcelableArrayList(KEY_BOARDS, ArrayList(items))
//                }
//            }
//        }
    }
}