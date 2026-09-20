package com.github.enteraname74.domain.model.statistics

import com.github.enteraname74.domain.model.LocalMonthYear
import kotlinx.serialization.Serializable

@Serializable
data class CloudLocalMonthYear(
    val month: Int,
    val year: Int,
) {
    fun toDomain(): LocalMonthYear =
        LocalMonthYear(
            month = month,
            year = year,
        )
}

internal fun LocalMonthYear.toCloud(): CloudLocalMonthYear =
    CloudLocalMonthYear(
        month = month,
        year = year,
    )