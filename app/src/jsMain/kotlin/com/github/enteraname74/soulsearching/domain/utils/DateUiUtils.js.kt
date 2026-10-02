package com.github.enteraname74.soulsearching.domain.utils

import kotlin.js.unsafeCast

actual object DateUiUtils {
    actual fun formatToReadableDate(millis: Long): String =
        formatWithIntl(millis = millis.toDouble(), timeStyle = "short")

    actual fun formatToReadableDateTime(millis: Long): String =
        formatWithIntl(millis = millis.toDouble(), timeStyle = "medium")
}

private fun formatWithIntl(millis: Double, timeStyle: String): String =
    js(
        """
            new Intl.DateTimeFormat(
                undefined,
                { dateStyle: "long", timeStyle: timeStyle }
            ).format(new Date(millis))
        """
    ).unsafeCast<String>()
