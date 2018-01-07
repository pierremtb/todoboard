package com.pierrejacquier.todoboard.screens.board

data class Block(
        val key: String,
        val layout: Int,
        val type: Int,
        var height: Int,
        var itemsCount: Int = 0
)