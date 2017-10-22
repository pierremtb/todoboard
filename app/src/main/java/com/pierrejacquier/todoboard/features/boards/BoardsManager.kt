package com.pierrejacquier.todoboard.features.boards

import com.pierrejacquier.todoboard.database.boards.Board
import com.pierrejacquier.todoboard.database.boards.BoardsDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardsManager @Inject constructor(val boardsDao: BoardsDao) {

    fun getBoards() = boardsDao.getBoards()
    fun addBoard(board: Board) = boardsDao.insertBoard(board)
}