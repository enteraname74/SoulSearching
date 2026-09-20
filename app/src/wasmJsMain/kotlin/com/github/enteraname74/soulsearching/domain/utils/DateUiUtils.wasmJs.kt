package com.github.enteraname74.soulsearching.domain.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

actual object DateUiUtils {
    actual fun formatToReadableDate(millis: Long): String =
        millis.toUtcReadableDate()

    actual fun formatToReadableDateTime(millis: Long): String {
        val dateTime = Instant
            .fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        return buildString {
            append(dateTime.day.toString().padStart(2, '0'))
            append('/')
            append(dateTime.month.number.toString().padStart(2, '0'))
            append('/')
            append(dateTime.year)
            append(' ')
            append(dateTime.hour.toString().padStart(2, '0'))
            append(':')
            append(dateTime.minute.toString().padStart(2, '0'))
            append(':')
            append(dateTime.second.toString().padStart(2, '0'))
        }
    }
}

private fun Long.toUtcReadableDate(): String {
    val isoDate = Instant.fromEpochMilliseconds(this).toString()
    val date = isoDate.substringBefore("T")
    val time = isoDate.substringAfter("T").take(5)
    return "$date $time"
}
