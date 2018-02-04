package com.pierrejacquier.todoboard.data.api

import com.pierrejacquier.todoboard.commons.extensions.log
import com.pierrejacquier.todoboard.commons.extensions.toBool
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.todoist.TodoistSyncResponse
import e
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SyncService(val todoistApi: TodoistApi, val database: AppDatabase) {

    private var subscriptions = CompositeDisposable()

    var syncing = false

    fun sync(board: Board, forceFullSync: Boolean = false): Single<Board> {
        return Single.create { emitter ->
                val subscription = todoistApi.sync(
                        board.accessToken,
                        if (!forceFullSync) board.syncToken else "*"
                )
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                            {  handleResponse(it, emitter, board) },
                            { err -> e { err.message ?: "" } }
                        )
                subscriptions.add(subscription)
                syncing = true
        }
    }

    private fun handleResponse(res: TodoistSyncResponse, emitter: SingleEmitter<Board>, board: Board) {
        val (user, items, projects, fullSync, syncToken) = res
        board.syncToken = syncToken

        if (user != null) {
            board.userId = user.id
            user.token = board.accessToken

            for (project in projects) {
                project.userId = user.id
            }
        }

        if (fullSync) {
            with(database) {
                user?.let {
                    val userSub = usersDao().isUser(user.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { isUser ->
                            val dbSub = Observable.fromCallable {
                                        if (!isUser) usersDao().insertUser(user)
                                        projectsDao().insertProjects(projects)
                                        itemsDao().insertItems(items)
                                        boardsDao().updateBoard(board)
                                    }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        emitter.onSuccess(board)
                                    }
                            subscriptions.add(dbSub)
                        }
                    subscriptions.add(userSub)
                }
            }
        } else {
            val deletedItems = items.filter { it.isDeleted.toBool() }
            val newItems = items.filter { !it.isDeleted.toBool() }
            val deletedProjects = projects.filter { it.isDeleted.toBool() }
            val newProjects = projects.filter { !it.isDeleted.toBool() }

            with(database) {
                val dbSub = Observable.fromCallable {
                            user?.let { usersDao().updateUser(it) }
                            if (newItems.isNotEmpty()) {
                                itemsDao().insertItems(newItems)
                            }
                            if (deletedItems.isNotEmpty()) {
                                itemsDao().deleteItems(deletedItems)
                            }
                            if (newProjects.isNotEmpty()) {
                                projectsDao().insertProjects(newProjects)
                            }
                            if (deletedItems.isNotEmpty()) {
                                projectsDao().deleteProjects(deletedProjects)
                            }
                            boardsDao().updateBoard(board)
                        }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { emitter.onSuccess(board) }
                subscriptions.add(dbSub)
            }
        }
    }
}