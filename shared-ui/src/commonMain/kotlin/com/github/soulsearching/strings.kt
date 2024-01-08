package com.github.soulsearching

import androidx.compose.ui.text.intl.Locale

val strings = when(Locale.current.language) {
    "fr" -> FrStrings
    else -> EnStrings
}

/**
 * Application strings.
 */
interface Strings {
    val appName: String get() = "Soul Searching"
    val noElements: String
    val backButton: String
    val headerBarRightButton: String
    val image: String
}

/**
 * French translation for application strings
 */
object FrStrings : Strings {
    override val noElements = "Aucun élément"
    override val backButton = "Bouton de retour"
    override val headerBarRightButton = "Bouton droit de la bar d'état"
    override val image = "Image"
}

/**
 * English translation for application strings
 */
object EnStrings : Strings {
    override val noElements = "No elements"
    override val backButton = "Back button"
    override val headerBarRightButton = "Header bar right button"
    override val image = "Image"
}