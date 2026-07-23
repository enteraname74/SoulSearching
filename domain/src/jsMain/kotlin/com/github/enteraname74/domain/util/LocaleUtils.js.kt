package com.github.enteraname74.domain.util

actual object LocaleUtils {
    actual fun currentLanguage(): String =
        browserLanguage().takeIf { it.isNotBlank() } ?: "en"
}

private external val navigator: BrowserNavigator

private external interface BrowserNavigator {
    val language: String?
}

private fun browserLanguage(): String =
    navigator.language.orEmpty()
