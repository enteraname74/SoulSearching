package com.github.enteraname74.soulsearching.coreui.theme.color

enum class ColorPaletteSeed {
    DarkVibrant,
    DarkMuted,
    LightVibrant,
    LightMuted,
    Dominant,
    Vibrant,
    Muted;

    companion object {
        fun fromString(string: String): ColorPaletteSeed? =
            entries.find { it.toString() == string }

        val Default = DarkVibrant
    }
}