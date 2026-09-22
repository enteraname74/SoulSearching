package com.github.enteraname74.domain.util

import java.util.Locale

actual object LocaleUtils {
    actual fun currentLanguage(): String =
        Locale.getDefault().language
}
