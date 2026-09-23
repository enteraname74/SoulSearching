package com.github.enteraname74.soulsearching.domain.model

data class LocalMonthYear(
    val month: Int,
    val year: Int,
) {
    override fun toString(): String =
        "$month-$year"
}
