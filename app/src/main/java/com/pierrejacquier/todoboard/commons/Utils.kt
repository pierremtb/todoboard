package com.pierrejacquier.todoboard.commons

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}