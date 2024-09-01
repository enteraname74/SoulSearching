package com.github.enteraname74.soulsearching.coreui.theme.color

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingElement
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.model.settings.settingElementOf
import com.github.enteraname74.soulsearching.coreui.strings.strings

data class SoulSearchingDarkLightTheme(
    val id: SoulSearchingTheme,
    val darkTheme: SoulSearchingPalette,
    val lightTheme: SoulSearchingPalette,
    val name: @Composable () -> String,
) {
    fun palette(isInDarkTheme: Boolean): SoulSearchingPalette =
        if (isInDarkTheme) darkTheme else lightTheme
}

object SoulSearchingDarkLightThemes {

    private val mainTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.MainTheme,
        darkTheme = SoulSearchingPalettes.darkTheme,
        lightTheme = SoulSearchingPalettes.lightTheme,
        name = { strings.mainTheme }
    )

    private val steelTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.SteelTheme,
        darkTheme = SoulSearchingPalettes.darkSteel,
        lightTheme = SoulSearchingPalettes.lightSteel,
        name = { strings.steelTheme },
    )

    private val glacierTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.GlacierTheme,
        darkTheme = SoulSearchingPalettes.darkGlacier,
        lightTheme = SoulSearchingPalettes.lightGlacier,
        name = { strings.glacierTheme },
    )

    private val duskTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.DuskTheme,
        darkTheme = SoulSearchingPalettes.darkDusk,
        lightTheme = SoulSearchingPalettes.lightDusk,
        name = { strings.duskTheme }
    )

    private val passionTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.PassionTheme,
        darkTheme = SoulSearchingPalettes.darkPassion,
        lightTheme = SoulSearchingPalettes.lightPassion,
        name = { strings.passionTheme }
    )

    private val greeneryTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.GreeneryTheme,
        darkTheme = SoulSearchingPalettes.darkGreenery,
        lightTheme = SoulSearchingPalettes.lightGreenery,
        name = { strings.greeneryTheme }
    )

    private val treeBarkTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.TreeBarkTheme,
        darkTheme = SoulSearchingPalettes.darkTreeBark,
        lightTheme = SoulSearchingPalettes.lightTreeBark,
        name = { strings.treeBarkTheme }
    )

    val themes: List<SoulSearchingDarkLightTheme> = listOf(
        mainTheme,
        steelTheme,
        glacierTheme,
        duskTheme,
        passionTheme,
        greeneryTheme,
        treeBarkTheme,
    )

    fun fromId(id: SoulSearchingTheme): SoulSearchingDarkLightTheme =
        themes.find { it.id == id } ?: mainTheme
}

val SoulSearchingSettingsKeys.ColorTheme.USED_COLOR_THEME_ID_KEY: SoulSearchingSettingElement<String>
    get() = settingElementOf(
        key = "USED_COLOR_THEME_ID_KEY",
        defaultValue = SoulSearchingTheme.MainTheme.value,
    )
