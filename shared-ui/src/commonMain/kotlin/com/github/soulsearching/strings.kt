package com.github.soulsearching

import androidx.compose.ui.text.intl.Locale

val strings = when(Locale.current.language) {
    "fr" -> FrStrings
    else -> EnStrings
}

interface Strings {
    val appName: String get() = "Soul Searching"
    val noElements: String
}

object FrStrings : Strings {
    override val noElements = "Aucun élément"
}

object EnStrings : Strings {
    override val noElements = "No elements"
}