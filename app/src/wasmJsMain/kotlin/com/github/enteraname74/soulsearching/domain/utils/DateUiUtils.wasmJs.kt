package com.github.enteraname74.soulsearching.domain.utils

import kotlin.time.Instant

actual object DateUiUtils {
    actual fun formatToReadableDate(millis: Long): String =
        millis.toUtcReadableDate()
}

private fun Long.toUtcReadableDate(): String {
    val isoDate = Instant.fromEpochMilliseconds(this).toString()
    val date = isoDate.substringBefore("T")
    val time = isoDate.substringAfter("T").take(5)
    return "$date $time"
}
