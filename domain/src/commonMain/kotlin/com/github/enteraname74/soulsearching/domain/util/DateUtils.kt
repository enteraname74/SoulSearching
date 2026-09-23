package com.github.enteraname74.soulsearching.domain.util

import com.github.enteraname74.soulsearching.domain.model.LocalMonthYear
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

object DateUtils {
    fun now(): Long = Clock.System.now().toEpochMilliseconds()

    fun currentMonthYear(): LocalMonthYear {
        val localDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        return LocalMonthYear(
            month = localDateTime.month.number,
            year = localDateTime.year,
        )
    }
}
