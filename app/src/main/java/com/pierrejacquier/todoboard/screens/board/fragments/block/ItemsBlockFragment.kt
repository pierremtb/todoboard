package com.pierrejacquier.todoboard.screens.board.fragments.block

import android.graphics.Point
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.TodoboardApp
import com.pierrejacquier.todoboard.commons.RxBaseFragment
import com.pierrejacquier.todoboard.commons.extensions.log
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.todoist.Item
import com.pierrejacquier.todoboard.databinding.BoardFragmentItemsBlockBinding
import com.pierrejacquier.todoboard.screens.board.BoardActivity
import com.pierrejacquier.todoboard.screens.board.ItemsManager
import com.pierrejacquier.todoboard.screens.board.di.BoardActivityComponent
import com.pierrejacquier.todoboard.screens.board.fragments.block.adapters.ItemsAdapter
import com.pierrejacquier.todoboard.screens.board.fragments.block.di.DaggerItemsBlockFragmentComponent
import com.pierrejacquier.todoboard.screens.board.fragments.header.HeaderFragment.Companion.KEY_USER
import com.pierrejacquier.todoboard.screens.board.fragments.header.di.DaggerHeaderFragmentComponent
import e
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_fragment_boards.*
import kotlinx.android.synthetic.main.board_fragment_items_block.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ItemsBlockFragment : RxBaseFragment() {

    companion object {
        const val KEY_TYPE = "type"

        const val OVERDUE = 0
        const val TODAY = 1
        const val TOMORROW = 2
        const val LATER = 3
        const val UNDATED = 4
    }

    private lateinit var TITLES: Array<String>

    lateinit var binding: BoardFragmentItemsBlockBinding

    var type = 0

    @Inject
    lateinit var itemsManager: ItemsManager

    private lateinit var itemsAdapter: ItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerItemsBlockFragmentComponent.builder()
                .boardActivityComponent((activity as BoardActivity).component)
                .build()
                .inject(this)

        with(context?.resources) {
            TITLES = arrayOf(
                getString(R.string.overdue),
                getString(R.string.today),
                getString(R.string.tomorrow),
                getString(R.string.later),
                getString(R.string.undated)
            )
        }

        arguments?.let {
            type = it.getInt(KEY_TYPE)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BoardFragmentItemsBlockBinding.inflate(inflater, container, false)
        binding.title = TITLES[type]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)

        itemsAdapter = ItemsAdapter(size.x)

        with (itemsRV) {
//            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
            (itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        }
        val itemsSub = itemsManager.getItemsObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { itemsAdapter.items = it }

        subscriptions.add(itemsSub)
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