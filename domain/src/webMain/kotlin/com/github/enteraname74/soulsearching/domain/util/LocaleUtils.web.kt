package com.github.enteraname74.soulsearching.domain.util

import kotlinx.browser.window

actual object LocaleUtils {
    actual fun currentLanguage(): String =
        window.navigator.language.takeIf { it.isNotBlank() } ?: "en"
}
