package com.pierrejacquier.todoboard.data.api

import android.support.annotation.MainThread
import com.pierrejacquier.todoboard.commons.extensions.toBool
import com.pierrejacquier.todoboard.data.database.AppDatabase
import com.pierrejacquier.todoboard.data.model.Board
import e
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class SyncService(val todoistApi: TodoistApi, val database: AppDatabase) {

    private var subscriptions = CompositeDisposable()

    var syncing = false

    fun sync(board: Board, forceFullSync: Boolean = false): Single<Board> {
        return Single.create { emitter ->
            val projectsSub = database.projectsDao().findBoardProjectsSingle(board.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { currentProjects ->
                        val subscription = todoistApi.sync(board.accessToken,
                                if (!forceFullSync) board.syncToken else "*")
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { res ->
                                            val (user, items, projects, fullSync, syncToken) = res
                                            board.syncToken = syncToken

                                            if (user != null) {
                                                user.boardId = board.id
                                                board.userId = user.id
                                            }

                                            for (project in projects) {
                                                project.boardId = board.id
                                                project.userId = board.userId
                                            }

                                            for (currentProject in currentProjects) {
                                                val sameNewProject = projects.findLast { it.id == currentProject.id }
                                                sameNewProject?.let {
                                                    it.selected = currentProject.selected
                                                }
                                            }

                                            for (item in items) {
                                                item.boardId = board.id
                                            }

                                            if (fullSync) {
                                                with(database) {
                                                    subscriptions.add(
                                                            Observable.fromCallable {
                                                                user?.let { usersDao().insertUser(it) }
                                                                projectsDao().forceSyncBoardProjects(board.id, projects)
                                                                itemsDao().forceSyncBoardItems(board.id, items)
                                                                boardsDao().updateBoard(board)
                                                            }
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribe {
                                                                        emitter.onSuccess(board)
                                                                    }
                                                    )
                                                }
                                            } else {
                                                val deletedItems = items.filter { it.isDeleted.toBool() }
                                                val newItems = items.filter { !it.isDeleted.toBool() }
                                                e { "////// INC SYNC RESULT" }
                                                e { deletedItems.map { it.content }.toString() }
                                                e { newItems.map { it.content }.toString() }
                                                e { "////// END"}

                                                val deletedProjects = projects.filter { it.isDeleted.toBool() }
                                                val newProjects = projects.filter { !it.isDeleted.toBool() }

                                                with(database) {
                                                    subscriptions.add(
                                                            Observable.fromCallable {
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
                                                                    .subscribe {
                                                                        emitter.onSuccess(board)
                                                                    }
                                                    )
                                                }
                                            }
                                        },
                                        { err ->
                                            e { err.toString() }
                                            emitter.onError(Throwable(err.message.toString()))
                                        }
                                )
                        subscriptions.add(subscription)
                    }
            subscriptions.add(projectsSub)
            syncing = true
        }
    }
}