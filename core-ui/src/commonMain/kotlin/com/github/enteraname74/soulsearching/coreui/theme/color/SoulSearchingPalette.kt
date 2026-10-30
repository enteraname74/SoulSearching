package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.compose.ui.graphics.Color

/**
 * Color theme for SoulSearching.
 *
 * @param background the main background of the screens.
 * @param surfaceLow a surface between [background] and [surface], used for navigation panels.
 * @param surface the surface used for cards, sheets and grouped content.
 * @param surfaceHigh a surface above [surface], used for selected or highlighted elements.
 * @param outline a subtle color used for dividers and borders.
 * @param text the main text color, on [background] and on every surface.
 * @param textSecondary the secondary text color, for subtitles and supporting content.
 * @param textTertiary a discreet text color for metadata and hints.
 * @param accent the color of primary actions and selected states.
 * @param onAccent the content color on [accent].
 * @param danger the color of destructive actions and errors.
 * @param onDanger the content color on [danger].
 * @param warning the color of warnings and actions that need attention.
 * @param onWarning the content color on [warning].
 */
data class SoulSearchingPalette(
    val background: Color,
    val surfaceLow: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val outline: Color,
    val text: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accent: Color,
    val onAccent: Color,
    val danger: Color,
    val onDanger: Color,
    val warning: Color,
    val onWarning: Color,
)

object SoulSearchingPalettes {
    val darkTheme = SoulSearchingPalette(
        background = Color(0xFF0E0C24),
        surface = Color(0xFF1B1840),
        text = Color(0xFFBFC3D2),
        textSecondary = Color(0xFF9A9EB4),
        surfaceLow = Color(0xFF141232),
        surfaceHigh = Color(0xFF252256),
        outline = Color(0x14FFFFFF),
        textTertiary = Color(0xFF7E8299),
        accent = Color(0xFF3CC8C8),
        onAccent = Color(0xFF042322),
        danger = Color(0xFFFF8A93),
        onDanger = Color(0xFF0E0C24),
        warning = Color(0xFFFFC266),
        onWarning = Color(0xFF0E0C24),
    )

    val lightTheme = SoulSearchingPalette(
        background = Color(0xFFFDFDFE),
        surface = Color(0xFFF1F1F4),
        text = Color(0xFF212121),
        textSecondary = Color(0xFF514141),
        surfaceLow = Color(0xFFF7F7F9),
        surfaceHigh = Color(0xFFE8E8EE),
        outline = Color(0x1A212121),
        textTertiary = Color(0xFF6E6464),
        accent = Color(0xFF0F7C80),
        onAccent = Color(0xFFFFFFFF),
        danger = Color(0xFFC2253A),
        onDanger = Color(0xFFFFFFFF),
        warning = Color(0xFF9A5B00),
        onWarning = Color(0xFFFFFFFF),
    )

    val darkSteel = SoulSearchingPalette(
        background = Color(0xFF161215),
        surface = Color(0xFF272126),
        text = Color(0xFFC9CBC8),
        textSecondary = Color(0xFFA4A1A3),
        surfaceLow = Color(0xFF1E191D),
        surfaceHigh = Color(0xFF332B32),
        outline = Color(0x14FFFFFF),
        textTertiary = Color(0xFF858083),
        accent = Color(0xFFB8C7D6),
        onAccent = Color(0xFF161215),
        danger = Color(0xFFFF8A8A),
        onDanger = Color(0xFF161215),
        warning = Color(0xFFFFC266),
        onWarning = Color(0xFF161215),
    )

    val lightSteel = SoulSearchingPalette(
        background = Color(0xFFF5F6F5),
        surface = Color(0xFFE2E5E3),
        text = Color(0xFF1C161B),
        textSecondary = Color(0xFF4B4B4B),
        surfaceLow = Color(0xFFECEEEC),
        surfaceHigh = Color(0xFFD3D7D5),
        outline = Color(0x1A1C161B),
        textTertiary = Color(0xFF696869),
        accent = Color(0xFF41566B),
        onAccent = Color(0xFFFFFFFF),
        danger = Color(0xFFB3261E),
        onDanger = Color(0xFFFFFFFF),
        warning = Color(0xFF9A5B00),
        onWarning = Color(0xFFFFFFFF),
    )

    val darkGlacier = SoulSearchingPalette(
        background = Color(0xFF0B1325),
        surface = Color(0xFF162242),
        text = Color(0xFFC3CAE3),
        textSecondary = Color(0xFF97A3C4),
        surfaceLow = Color(0xFF101A33),
        surfaceHigh = Color(0xFF1E2D55),
        outline = Color(0x14E8EDFF),
        textTertiary = Color(0xFF7886AA),
        accent = Color(0xFF7DB4FF),
        onAccent = Color(0xFF06142E),
        danger = Color(0xFFFF8A9A),
        onDanger = Color(0xFF0B1325),
        warning = Color(0xFFFFC266),
        onWarning = Color(0xFF0B1325),
    )

    val lightGlacier = SoulSearchingPalette(
        background = Color(0xFFF7FAFF),
        surface = Color(0xFFE3EAFF),
        text = Color(0xFF152D69),
        textSecondary = Color(0xFF2F4D92),
        surfaceLow = Color(0xFFEEF3FF),
        surfaceHigh = Color(0xFFD2DDFF),
        outline = Color(0x1F152D69),
        textTertiary = Color(0xFF4E64A0),
        accent = Color(0xFF2F5BD3),
        onAccent = Color(0xFFFFFFFF),
        danger = Color(0xFFC42B3F),
        onDanger = Color(0xFFFFFFFF),
        warning = Color(0xFF9A5B00),
        onWarning = Color(0xFFFFFFFF),
    )

    val darkDusk = SoulSearchingPalette(
        background = Color(0xFF2B1404),
        surface = Color(0xFF47230B),
        text = Color(0xFFE3D3C4),
        textSecondary = Color(0xFFC8A68A),
        surfaceLow = Color(0xFF381B07),
        surfaceHigh = Color(0xFF5A2D10),
        outline = Color(0x14FFF0E3),
        textTertiary = Color(0xFFA9876C),
        accent = Color(0xFFFF9F4A),
        onAccent = Color(0xFF2B1404),
        danger = Color(0xFFFF7A90),
        onDanger = Color(0xFF2B1404),
        // The accent is already orange, so warnings lean yellow to stay distinguishable.
        warning = Color(0xFFFFD966),
        onWarning = Color(0xFF2B1404),
    )

    val lightDusk = SoulSearchingPalette(
        background = Color(0xFFFFF7F0),
        surface = Color(0xFFFFE3CB),
        text = Color(0xFF4E2400),
        textSecondary = Color(0xFF843E00),
        surfaceLow = Color(0xFFFFEEDF),
        surfaceHigh = Color(0xFFFFD2AC),
        outline = Color(0x1F4E2400),
        textTertiary = Color(0xFF9A5A2A),
        accent = Color(0xFFB85300),
        onAccent = Color(0xFFFFFFFF),
        danger = Color(0xFFBE1F3C),
        onDanger = Color(0xFFFFFFFF),
        warning = Color(0xFF8A6A00),
        onWarning = Color(0xFFFFFFFF),
    )

    val darkPassion = SoulSearchingPalette(
        background = Color(0xFF2A0508),
        surface = Color(0xFF460C12),
        text = Color(0xFFE8C8C8),
        textSecondary = Color(0xFFD49A9A),
        surfaceLow = Color(0xFF36080C),
        surfaceHigh = Color(0xFF5A121A),
        outline = Color(0x14FFE4E4),
        textTertiary = Color(0xFFB47E7E),
        accent = Color(0xFFFF5C68),
        onAccent = Color(0xFF2A0508),
        // The accent is already red, so destructive actions use amber to stay distinguishable.
        danger = Color(0xFFFFB65C),
        onDanger = Color(0xFF2A0508),
        // Danger already uses amber, so warnings lean yellow to stay distinguishable.
        warning = Color(0xFFFFE27A),
        onWarning = Color(0xFF2A0508),
    )

    val lightPassion = SoulSearchingPalette(
        background = Color(0xFFFFF6F6),
        surface = Color(0xFFFFE0E0),
        text = Color(0xFF550000),
        textSecondary = Color(0xFF790000),
        surfaceLow = Color(0xFFFFEDED),
        surfaceHigh = Color(0xFFFFCBCB),
        outline = Color(0x1A550000),
        textTertiary = Color(0xFF8E4A4A),
        accent = Color(0xFFC8203A),
        onAccent = Color(0xFFFFFFFF),
        danger = Color(0xFFA33A00),
        onDanger = Color(0xFFFFFFFF),
        warning = Color(0xFF806400),
        onWarning = Color(0xFFFFFFFF),
    )

    val darkGreenery = SoulSearchingPalette(
        background = Color(0xFF03150A),
        surface = Color(0xFF082A14),
        text = Color(0xFFBFE3CB),
        textSecondary = Color(0xFF80CF9C),
        surfaceLow = Color(0xFF061E0F),
        surfaceHigh = Color(0xFF0D381C),
        outline = Color(0x14DFFFEA),
        textTertiary = Color(0xFF5FAA7B),
        accent = Color(0xFF4BE07D),
        onAccent = Color(0xFF03150A),
        danger = Color(0xFFFF8A8A),
        onDanger = Color(0xFF03150A),
        warning = Color(0xFFFFC266),
        onWarning = Color(0xFF03150A),
    )

    val lightGreenery = SoulSearchingPalette(
        background = Color(0xFFF2FFF6),
        surface = Color(0xFFD5F7E0),
        text = Color(0xFF003A11),
        textSecondary = Color(0xFF004F17),
        surfaceLow = Color(0xFFE6FCEE),
        surfaceHigh = Color(0xFFBDF0CD),
        outline = Color(0x1F003A11),
        textTertiary = Color(0xFF2F6B43),
        accent = Color(0xFF0E7F35),
        onAccent = Color(0xFFFFFFFF),
        danger = Color(0xFFC2253A),
        onDanger = Color(0xFFFFFFFF),
        warning = Color(0xFF9A5B00),
        onWarning = Color(0xFFFFFFFF),
    )

    val darkTreeBark = SoulSearchingPalette(
        background = Color(0xFF200C09),
        surface = Color(0xFF361711),
        text = Color(0xFFE3CBC7),
        textSecondary = Color(0xFFC48E89),
        surfaceLow = Color(0xFF2A110D),
        surfaceHigh = Color(0xFF451E17),
        outline = Color(0x14FFE9E6),
        textTertiary = Color(0xFFA27069),
        accent = Color(0xFFEB8D70),
        onAccent = Color(0xFF200C09),
        // Leans crimson-pink to stay distinguishable from the terracotta accent.
        danger = Color(0xFFFF7AA0),
        onDanger = Color(0xFF200C09),
        warning = Color(0xFFFFC266),
        onWarning = Color(0xFF200C09),
    )

    val lightTreeBark = SoulSearchingPalette(
        background = Color(0xFFFFF4F2),
        surface = Color(0xFFFFDFD8),
        text = Color(0xFF440700),
        textSecondary = Color(0xFF640B00),
        surfaceLow = Color(0xFFFFEBE7),
        surfaceHigh = Color(0xFFF7CBC0),
        outline = Color(0x1F440700),
        textTertiary = Color(0xFF7E3A30),
        accent = Color(0xFFA3412B),
        onAccent = Color(0xFFFFFFFF),
        danger = Color(0xFFB0174B),
        onDanger = Color(0xFFFFFFFF),
        warning = Color(0xFF8A6A00),
        onWarning = Color(0xFFFFFFFF),
    )
}
