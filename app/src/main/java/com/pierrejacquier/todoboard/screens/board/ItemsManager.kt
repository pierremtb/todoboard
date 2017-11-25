package com.pierrejacquier.todoboard.screens.board

import com.pierrejacquier.todoboard.commons.extensions.getDueDate
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.todoist.Item
import com.pierrejacquier.todoboard.screens.board.fragments.block.ItemsBlockFragment
import e
import io.reactivex.Observable
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
            e { "////// MANAGER ITEMS SET" }
            e { newItems.map { it.content }.toString() }
            e { "////// END"}
            refreshSubject.onNext(true)
        }

    var observable: Observable<List<Item>>

    fun getItemsObservable(type: Int): Observable<List<Item>> =
            observable.map { items -> items.filter(getFilter(type)) }

    init {
        observable = mainObservable.mergeWith(refreshSubject.flatMap { _ -> mainObservable })
    }

    private fun getFilter(type: Int): (Item) -> Boolean {
        when (type) {
            ItemsBlockFragment.TODAY -> return { checkToday(it) }
            ItemsBlockFragment.TOMORROW -> return { checkTomorrow(it) }
            ItemsBlockFragment.UNDATED -> return { it.dueDateUtc.isNullOrEmpty() }
        }
        return { true }
    }
    //Tue 07 Nov 2017 03:34:59 +0000

    private fun checkToday(item: Item): Boolean {
        item.dueDateUtc?.let {
            return item.getDueDate() in Dates.today.beginningOfDay..Dates.today.endOfDay
        }
        return false
    }

    private fun checkTomorrow(item: Item): Boolean {
        item.dueDateUtc?.let {
            return item.getDueDate() in Dates.tomorrow.beginningOfDay..Dates.tomorrow.endOfDay
        }
        return false
    }
}