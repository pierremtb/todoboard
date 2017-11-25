package com.pierrejacquier.todoboard.screens.board.fragments.block

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierrejacquier.todoboard.TodoboardApp
import com.pierrejacquier.todoboard.commons.RxBaseFragment
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.todoist.Item
import com.pierrejacquier.todoboard.databinding.FragmentHeaderBinding
import com.pierrejacquier.todoboard.databinding.FragmentItemsBlockBinding
import com.pierrejacquier.todoboard.screens.board.BoardActivity
import com.pierrejacquier.todoboard.screens.board.ItemsManager
import com.pierrejacquier.todoboard.screens.board.di.BoardActivityComponent
import com.pierrejacquier.todoboard.screens.board.fragments.block.adapters.ItemsAdapter
import com.pierrejacquier.todoboard.screens.board.fragments.block.di.DaggerItemsBlockFragmentComponent
import com.pierrejacquier.todoboard.screens.board.fragments.header.HeaderFragment.Companion.KEY_USER
import com.pierrejacquier.todoboard.screens.board.fragments.header.di.DaggerHeaderFragmentComponent
import e
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_boards.*
import kotlinx.android.synthetic.main.fragment_items_block.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ItemsBlockFragment : RxBaseFragment() {

    companion object {
        const val KEY_TYPE = "type"

        const val TODAY = 0
        const val TOMORROW = 1
        const val LATER = 2
        const val UNDATED = 3

        val TITLES = arrayOf("Today", "Tomorrow", "Later", "Undated")
    }

    lateinit var binding: FragmentItemsBlockBinding

    var type = 0

    @Inject
    lateinit var itemsManager: ItemsManager

    var items: List<Item>
        get() = with(itemsRV.adapter as ItemsAdapter) { return items }
        set(newItems) {
            with(itemsRV.adapter as ItemsAdapter) { items = newItems }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerItemsBlockFragmentComponent.builder()
                .boardActivityComponent((activity as BoardActivity).component)
                .build()
                .inject(this)

        arguments?.let {
            type = it.getInt(KEY_TYPE)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentItemsBlockBinding.inflate(inflater, container, false)
        binding.title = TITLES[type]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (itemsRV) {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = ItemsAdapter()
        }

        val itemsSub = itemsManager.getItemsObservable(type)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { newItems ->
                    activity?.runOnUiThread {
                        items = newItems
                        e { "////// ${TITLES[type]} "}
                        e { newItems.map { it.content }.toString() }
                        e { "////// end" }
                    }
                }

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