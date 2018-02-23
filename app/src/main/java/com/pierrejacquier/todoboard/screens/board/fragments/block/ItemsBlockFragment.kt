package com.pierrejacquier.todoboard.screens.board.fragments.block

import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.commons.RxBaseFragment
import com.pierrejacquier.todoboard.commons.extensions.dp
import com.pierrejacquier.todoboard.commons.extensions.log
import com.pierrejacquier.todoboard.databinding.BoardFragmentItemsBlockBinding
import com.pierrejacquier.todoboard.screens.board.BoardActivity
import com.pierrejacquier.todoboard.screens.board.BoardActivity.Companion.TITLE_HEIGHT_SUP
import com.pierrejacquier.todoboard.screens.board.ItemsManager
import com.pierrejacquier.todoboard.screens.board.fragments.block.adapters.ItemsAdapter
import com.pierrejacquier.todoboard.screens.board.fragments.block.di.DaggerItemsBlockFragmentComponent
import com.pierrejacquier.todoboard.screens.details.BoardDetailsActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.board_fragment_items_block.*
import kotlinx.android.synthetic.main.board_task_item.*
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

class ItemsBlockFragment : RxBaseFragment() {

    companion object {
        const val KEY_TYPE = "type"
        const val KEY_MULTI_COLUMNS = "mcol"
        const val KEY_FONT_SIZE = "fs"
        const val KEY_ALLOW_FOR_AUTO_SCROLL = "allow-auto-scroll"
        const val KEY_AUTO_SCROLL_DELAY = "auto-scroll-delay"

        const val OVERDUE = 0
        const val TODAY = 1
        const val TOMORROW = 2
        const val LATER = 3
        const val UNDATED = 4
        const val PROJECT = 5
    }

    private lateinit var titles: Array<String>

    lateinit var binding: BoardFragmentItemsBlockBinding

    var type = 0

    @Inject
    lateinit var itemsManager: ItemsManager

    private lateinit var itemsAdapter: ItemsAdapter

    private lateinit var autoScrollTimer: Timer

    var columns: Int = 1

    private var allowForMultiColumns: Boolean = true

    private var fontSize: Int = 18
    private var allowForAutoScroll = true
    private var autoScrollDelay: Int = BoardDetailsActivity.DEFAULT_AUTO_SCROLL_DELAY

    override fun onCreate(savedInstanceState: Bundle?) {
        retainInstance = true
        DaggerItemsBlockFragmentComponent.builder()
                .boardActivityComponent((activity as BoardActivity).component)
                .build()
                .inject(this)

        with(context?.resources) {
            titles = arrayOf(
                getString(R.string.overdue),
                getString(R.string.today),
                getString(R.string.tomorrow),
                getString(R.string.later),
                getString(R.string.undated)
            )
        }

        arguments?.let {
            type = it.getInt(KEY_TYPE)
            allowForMultiColumns = it.getBoolean(KEY_MULTI_COLUMNS)
            fontSize = it.getInt(KEY_FONT_SIZE)
            allowForAutoScroll = it.getBoolean(KEY_ALLOW_FOR_AUTO_SCROLL)
            autoScrollDelay = it.getInt(KEY_AUTO_SCROLL_DELAY)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BoardFragmentItemsBlockBinding.inflate(inflater, container, false)
        binding.title = titles[type]
        columns = getColumnsCount()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)

        itemsAdapter = ItemsAdapter(
                getWidth() / columns,
                fontSize,
                type == OVERDUE || type ==  LATER
        )

        with (itemsRV) {
            layoutManager = GridLayoutManager(context, columns)
            adapter = itemsAdapter
            (itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        }
        val itemsSub = itemsManager.getItemsObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { itemsAdapter.items = it }

        subscriptions.add(itemsSub)

        binding.type.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.toFloat())
        binding.type.layoutParams.height = (fontSize + TITLE_HEIGHT_SUP).dp(context!!)

        if (allowForAutoScroll) {
            startAutoScrollTimer()
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration?) {
        columns = getColumnsCount()
        itemsAdapter.screenWidth = getWidth()
        itemsRV.swapAdapter(itemsAdapter, false)
        super.onConfigurationChanged(newConfig)

    }

    private fun getColumnsCount(): Int {
        if (!allowForMultiColumns) {
            return columns
        }
        return resources.getInteger(R.integer.board_activity_columns_count)
    }

    private fun getWidth(): Int {
        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        "x:${size.x},y:${size.y}"
        return size.x
    }

    private fun startAutoScrollTimer() {
        val delay = (1000 * autoScrollDelay).toLong()
        autoScrollTimer = fixedRateTimer(
                name = "scroll-timer",
                initialDelay = delay,
                period = delay
        ) {
            activity?.runOnUiThread {
                if (itemsRV == null) return@runOnUiThread

                if (itemsRV.computeVerticalScrollRange() > itemsRV.height) {
                    with (itemsRV.layoutManager as GridLayoutManager) {
                        var currentPos = findFirstVisibleItemPosition()
                        if (findLastVisibleItemPosition() == itemsAdapter.items.size - 1) {
                            currentPos = -1
                        }
                        scrollToPositionWithOffset(currentPos + columns, 0)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::autoScrollTimer.isInitialized) {
            autoScrollTimer.cancel()
        }
    }
}