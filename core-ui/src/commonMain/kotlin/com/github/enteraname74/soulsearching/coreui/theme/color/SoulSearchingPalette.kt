package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.compose.ui.graphics.Color

/**
 * Color theme for SoulSearching.
 */
data class SoulSearchingPalette(
    val primary: Color,
    val secondary: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val subSecondaryText: Color,
    val subPrimaryText: Color,
)


object SoulSearchingPalettes {
    val darkTheme = SoulSearchingPalette(
        primary = primaryColorDark,
        secondary = secondaryColorDark,
        onPrimary = textColorDark,
        onSecondary = textColorDark,
        subSecondaryText = subTextColorDark,
        subPrimaryText = subTextColorDark,
    )
    val lightTheme = SoulSearchingPalette(
        primary = primaryColorLight,
        secondary = secondaryColorLight,
        onPrimary = textColorLight,
        onSecondary = textColorLight,
        subSecondaryText = subTextColorLight,
        subPrimaryText = subTextColorLight,
    )

    val darkSteel = SoulSearchingPalette(
        primary = Color(0xFF1c161b),
        secondary = Color(0xFF2C242C),
        onPrimary = Color(0xFFE3E9E4),
        onSecondary = Color(0xFFE3E9E4),
        subPrimaryText = Color(0xFFb6b6b6),
        subSecondaryText = Color(0xFFb6b6b6),
    )
    val lightSteel = SoulSearchingPalette(
        primary = Color(0xFFF0F3F0),
        secondary = Color(0xFFCFD1D0),
        onPrimary = Color(0xFF1c161b),
        onSecondary = Color(0xFF1c161b),
        subPrimaryText = Color(0xFF4b4b4b),
        subSecondaryText = Color(0xFF4b4b4b),
    )

    val darkGlacier = SoulSearchingPalette(
        primary = Color(0xFF0F172B),
        secondary = Color(0xFF192345),
        onPrimary = Color(0xFFe5ebff),
        onSecondary = Color(0xFFe5ebff),
        subPrimaryText = Color(0xFF9FA2AA),
        subSecondaryText = Color(0xFF9FA2AA),
    )
    val lightGlacier = SoulSearchingPalette(
        primary = Color(0xFFf7faff),
        secondary = Color(0xFFbbcaff),
        onPrimary = Color(0xFF152d69),
        onSecondary = Color(0xFF152d69),
        subSecondaryText = Color(0xFF2f4d92),
        subPrimaryText = Color(0xFF2f4d92),
    )

    val darkDusk = SoulSearchingPalette(
        primary = Color(0xFF632e03),
        secondary = Color(0xFFa04b04),
        onPrimary = Color(0xFFfff0e3),
        onSecondary = Color(0xFFfff0e3),
        subPrimaryText = Color(0xFFdeba9c),
        subSecondaryText = Color(0xFFdeba9c),
    )
    val lightDusk = SoulSearchingPalette(
        primary = Color(0xFFfff5ed),
        secondary = Color(0xFFffb87b),
        onPrimary = Color(0xFF602d00),
        onSecondary = Color(0xFF602d00),
        subPrimaryText = Color(0xFF843e00),
        subSecondaryText = Color(0xFF843e00),
    )

    val darkPassion = SoulSearchingPalette(
        primary = Color(0xFF470000),
        secondary = Color(0xFF720000),
        onPrimary = Color(0xFFffe4e4),
        onSecondary = Color(0xFFffe4e4),
        subPrimaryText = Color(0xFFe9abab),
        subSecondaryText = Color(0xFFe9abab),
    )
    val lightPassion = SoulSearchingPalette(
        primary = Color(0xFFfff4f4),
        secondary = Color(0xFFff3f3f),
        onPrimary = Color(0xFF550000),
        onSecondary = Color(0xFF550000),
        subPrimaryText = Color(0xFF790000),
        subSecondaryText = Color(0xFF790000),
    )

    val darkGreenery = SoulSearchingPalette(
        primary = Color(0xFF031a0b),
        secondary = Color(0xFF062c11),
        onPrimary = Color(0xFFdfffea),
        onSecondary = Color(0xFFdfffea),
        subPrimaryText = Color(0xFF93e9b1),
        subSecondaryText = Color(0xFF93e9b1),
    )
    val lightGreenery = SoulSearchingPalette(
        primary = Color(0xFFedfff3),
        secondary = Color(0xFF64ee8c),
        onPrimary = Color(0xFF003a11),
        onSecondary = Color(0xFF003a11),
        subPrimaryText = Color(0xFF004f17),
        subSecondaryText = Color(0xFF004f17),
    )

    val darkTreeBark = SoulSearchingPalette(
        primary = Color(0xFF270e0b),
        secondary = Color(0xFF3f1911),
        onPrimary = Color(0xFFffe9e6),
        onSecondary = Color(0xFFffe9e6),
        subPrimaryText = Color(0xFFd89e99),
        subSecondaryText = Color(0xFFd89e99),
    )
    val lightTreeBark = SoulSearchingPalette(
        primary = Color(0xFFffe9e6),
        secondary = Color(0xFFce7966),
        onPrimary = Color(0xFF440700),
        onSecondary = Color(0xFF440700),
        subPrimaryText = Color(0xFF640b00),
        subSecondaryText = Color(0xFF640b00),
    )
}