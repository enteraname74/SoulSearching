package com.github.enteraname74.domain.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    /**
     * Retrieve the month and the year of a date in MM/yyyy format.
     */
    fun getMonthAndYearOfDate(date: LocalDateTime): String {
        return date.format(DateTimeFormatter.ofPattern("MM/yyyy"))
    }
}