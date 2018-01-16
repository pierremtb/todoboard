package com.pierrejacquier.todoboard.screens.board

import android.support.v4.util.ArrayMap
import com.pierrejacquier.todoboard.commons.extensions.getDueDate
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.todoist.Item
import com.pierrejacquier.todoboard.screens.board.fragments.block.ItemsBlockFragment
import e
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import org.reactivestreams.Subscriber
import io.reactivex.subjects.PublishSubject
import khronos.Dates
import khronos.beginningOfDay
import khronos.day
import khronos.endOfDay
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class ItemsManager {

    var mainObservable: Observable<List<Item>> = Observable.create<List<Item>> { subscriber ->
        subscriber.onNext(items)
        sizesSubscriber.onNext(sectionsSizes)
    }

    var refreshSubject = PublishSubject.create<Boolean>()

    var items: List<Item> = emptyList()
        set(newItems) {
            field = newItems
            refreshSubject.onNext(true)
        }

    var observable: Observable<List<Item>>

    var sectionsSizes: Map<Int, Long> = ArrayMap()

    lateinit var sizesSubscriber: ObservableEmitter<Map<Int, Long>>

    var sizesObservable: Observable<Map<Int, Long>> = Observable.create<Map<Int, Long>> {
        sizesSubscriber = it
    }

    fun getItemsObservable(type: Int): Observable<List<Item>> =
            observable.map { items ->
                val newItems = items.filter(getFilter(type))
                sectionsSizes.plus(Pair(type, newItems.size))
                newItems
            }

    init {
        observable = mainObservable.mergeWith(refreshSubject.flatMap { _ -> mainObservable })
    }

    private fun getFilter(type: Int): (Item) -> Boolean {
        when (type) {
            ItemsBlockFragment.OVERDUE -> return { checkOverdue(it) }
            ItemsBlockFragment.TODAY -> return { checkToday(it) }
            ItemsBlockFragment.TOMORROW -> return { checkTomorrow(it) }
            ItemsBlockFragment.LATER -> return { checkLater(it) }
            ItemsBlockFragment.UNDATED -> return { it.dueDateUtc.isNullOrEmpty() }
        }
        return { true }
    }
    //Tue 07 Nov 2017 03:34:59 +0000

    private fun checkOverdue(item: Item): Boolean {
        item.dueDateUtc?.let {
            return item.getDueDate() < Date()
        }
        return false
    }

    private fun checkToday(item: Item): Boolean {
        item.dueDateUtc?.let {
            return (item.getDueDate() in Dates.today.beginningOfDay..Dates.today.endOfDay) && !checkOverdue(item)
        }
        return false
    }

    private fun checkTomorrow(item: Item): Boolean {
        item.dueDateUtc?.let {
            return item.getDueDate() in Dates.tomorrow.beginningOfDay..Dates.tomorrow.endOfDay
        }
        return false
    }

    private fun checkLater(item: Item): Boolean {
        item.dueDateUtc?.let {
            return item.getDueDate() > Dates.tomorrow.endOfDay
        }
        return false
    }
}