package com.github.enteraname74.localdb.ext

import com.github.enteraname74.domain.model.statistics.Period
import kotlinx.datetime.Month
import kotlinx.datetime.number

internal fun Period.Specific.months(): List<Int> =
    when (this) {
        is Period.Month -> listOf(month)
        is Period.Year -> Month.entries.map { it.number }
    }

internal fun Period.Specific.years(): List<Int> =
    when (this) {
        is Period.Month -> listOf(year)
        is Period.Year -> listOf(year)
    }