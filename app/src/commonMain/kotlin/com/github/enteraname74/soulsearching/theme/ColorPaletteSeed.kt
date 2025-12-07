package com.github.enteraname74.soulsearching.theme

enum class ColorPaletteSeed {
    DarkVibrant,
    DarkMuted,
    LightMuted,
    LightVibrant,
    Dominant,
    Muted,
    Vibrant;

    companion object {
        fun fromString(string: String): ColorPaletteSeed? =
            entries.find { it.toString() == string }

        val Default = DarkVibrant
    }
}