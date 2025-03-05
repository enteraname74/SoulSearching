package com.github.enteraname74.domain.ext

fun <T> List<T>.getFirstsOrMax(total: Int): List<T> {
    return if (total >= this.size) {
        this
    } else {
        this.subList(0, total)
    }
}