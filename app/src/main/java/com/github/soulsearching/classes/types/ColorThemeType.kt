package com.github.soulsearching.classes.types

/**
 * Define the type of color theme used for the application.
 */
object ColorThemeType {
    /**
     * Use the current played music for defining the color theme.
     */
    const val DYNAMIC = 0

    /**
     * Use the system theme for defining the color theme.
     */
    const val SYSTEM = 1

    /**
     * Use a personalized theme for defining the color theme.
     */
    const val PERSONALIZED = 2
}