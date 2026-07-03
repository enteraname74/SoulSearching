package com.github.enteraname74.soulsearching.domain.utils

import kotlin.js.unsafeCast

actual object DateUiUtils {
    actual fun formatToReadableDate(millis: Long): String =
        formatWithIntl(millis.toDouble())
}

private fun formatWithIntl(millis: Double): String =
    js(
        """
            new Intl.DateTimeFormat(
                undefined,
                { dateStyle: "long", timeStyle: "short" }
            ).format(new Date(millis))
        """
    ).unsafeCast<String>()
