package com.github.enteraname74.domain.util

import java.text.DateFormat
import java.util.Date
import kotlin.time.Clock

object DateUtils {
    fun now(): Long = Clock.System.now().toEpochMilliseconds()

    fun formatToReadableDate(millis: Long): String =
        DateFormat.getDateTimeInstance(
            DateFormat.LONG,
            DateFormat.SHORT
        ).format(Date(millis))
}