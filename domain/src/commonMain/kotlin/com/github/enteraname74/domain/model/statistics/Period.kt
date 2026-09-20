package com.github.enteraname74.domain.model.statistics

sealed interface Period {

    data object All : Period

    sealed interface Specific : Period
    data class Year(val year: Int) : Specific

    data class Month(
        val month: Int,
        val year: Int,
    ) : Specific
}