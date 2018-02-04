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
    }

    var refreshSubject = PublishSubject.create<Boolean>()

    var items: List<Item> = emptyList()
        set(newItems) {
            field = newItems
            refreshSubject.onNext(true)
        }

    var observable: Observable<List<Item>>

    fun getItemsObservable(type: Int, projectId: Long = 0): Observable<List<Item>> =
            observable.map { items ->
                val newItems = items.filter(getFilter(type, projectId))
//                sectionsSizes.plus(Pair(type, newItems.size))
                newItems
            }

    init {
        observable = mainObservable.mergeWith(refreshSubject.flatMap { _ -> mainObservable })
    }

    private fun getFilter(type: Int, projectId: Long = 0): (Item) -> Boolean {
        when (type) {
            ItemsBlockFragment.PROJECT -> return { it.projectId == projectId }
            ItemsBlockFragment.OVERDUE -> return { checkOverdue(it) }
            ItemsBlockFragment.TODAY -> return { checkToday(it) }
            ItemsBlockFragment.TOMORROW -> return { checkTomorrow(it) }
            ItemsBlockFragment.LATER -> return { checkLater(it) }
            ItemsBlockFragment.UNDATED -> return { it.dueDateUtc.isNullOrEmpty() }
        }
        return { true }
    }

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