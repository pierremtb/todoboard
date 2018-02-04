package com.pierrejacquier.todoboard.screens.board.fragments.project

import android.content.res.Configuration
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
import com.pierrejacquier.todoboard.data.model.todoist.Project
import com.pierrejacquier.todoboard.databinding.BoardFragmentItemsBlockBinding
import com.pierrejacquier.todoboard.databinding.BoardFragmentProjectBlockBinding
import com.pierrejacquier.todoboard.screens.board.BoardActivity
import com.pierrejacquier.todoboard.screens.board.ItemsManager
import com.pierrejacquier.todoboard.screens.board.di.BoardActivityComponent
import com.pierrejacquier.todoboard.screens.board.fragments.block.ItemsBlockFragment
import com.pierrejacquier.todoboard.screens.board.fragments.block.adapters.ItemsAdapter
import com.pierrejacquier.todoboard.screens.board.fragments.block.di.DaggerItemsBlockFragmentComponent
import com.pierrejacquier.todoboard.screens.board.fragments.block.di.DaggerProjectBlockFragmentComponent
import com.pierrejacquier.todoboard.screens.board.fragments.header.HeaderFragment.Companion.KEY_USER
import com.pierrejacquier.todoboard.screens.board.fragments.header.di.DaggerHeaderFragmentComponent
import com.pierrejacquier.todoboard.screens.board.fragments.project.adapters.ProjectItemsAdapter
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

class ProjectBlockFragment : RxBaseFragment() {

    companion object {
        const val KEY_PROJECT_ID = "project-id"
    }

    lateinit var binding: BoardFragmentProjectBlockBinding

    lateinit var project: Project

    @Inject
    lateinit var itemsManager: ItemsManager

    private lateinit var itemsAdapter: ProjectItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        retainInstance = true
        DaggerProjectBlockFragmentComponent.builder()
                .boardActivityComponent((activity as BoardActivity).component)
                .build()
                .inject(this)

        arguments?.let {
            project = it.getParcelable(KEY_PROJECT_ID)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BoardFragmentProjectBlockBinding
                 .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemsAdapter = ProjectItemsAdapter(getWidth())

        with (itemsRV) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
            (itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        }

        binding.title = project.name

        if (project.id != null) {
            val itemsSub = itemsManager.getItemsObservable(ItemsBlockFragment.PROJECT, project.id!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { itemsAdapter.items = it.sortedBy { it.itemOrder } }

            subscriptions.add(itemsSub)
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        itemsAdapter.screenWidth = getWidth()
        itemsRV.swapAdapter(itemsAdapter, false)
        super.onConfigurationChanged(newConfig)

    }

    private fun getWidth(): Int {
        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        return size.x
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        boardsRV?.let {
//            with(it.adapter as BoardsAdapter) {
//                if (items.isNotEmpty()) {
//                    outState.putParcelableArrayList(KEY_BOARDS, ArrayList(items))
//                }
//            }
//        }
//    }

}