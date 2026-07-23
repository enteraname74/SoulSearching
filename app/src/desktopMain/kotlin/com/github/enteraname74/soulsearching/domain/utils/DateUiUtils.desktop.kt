package com.github.enteraname74.soulsearching.domain.utils

import java.text.DateFormat
import java.util.Date

actual object DateUiUtils {
    actual fun formatToReadableDate(millis: Long): String =
        DateFormat.getDateTimeInstance(
            DateFormat.LONG,
            DateFormat.SHORT,
        ).format(Date(millis))
}
