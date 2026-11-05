package com.github.enteraname74.soulsearching.coreui.theme.color

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.dusk_theme
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.glacier_theme
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.greenery_theme
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.main_theme
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.passion_theme
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.steel_theme
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.tree_bark_theme
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingTheme


data class SoulSearchingDarkLightTheme(
    val id: SoulSearchingTheme,
    val darkTheme: SoulSearchingPalette,
    val lightTheme: SoulSearchingPalette,
    val name: SoulTextSpec,
) {
    fun palette(isInDarkTheme: Boolean): SoulSearchingPalette =
        if (isInDarkTheme) darkTheme else lightTheme
}

object SoulSearchingDarkLightThemes {

    private val mainTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.MainTheme,
        darkTheme = SoulSearchingPalettes.darkTheme,
        lightTheme = SoulSearchingPalettes.lightTheme,
        name = CoreRes.string.main_theme.toTextSpec()
    )

    private val steelTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.SteelTheme,
        darkTheme = SoulSearchingPalettes.darkSteel,
        lightTheme = SoulSearchingPalettes.lightSteel,
        name = CoreRes.string.steel_theme.toTextSpec(),
    )

    private val glacierTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.GlacierTheme,
        darkTheme = SoulSearchingPalettes.darkGlacier,
        lightTheme = SoulSearchingPalettes.lightGlacier,
        name = CoreRes.string.glacier_theme.toTextSpec(),
    )

    private val duskTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.DuskTheme,
        darkTheme = SoulSearchingPalettes.darkDusk,
        lightTheme = SoulSearchingPalettes.lightDusk,
        name = CoreRes.string.dusk_theme.toTextSpec()
    )

    private val passionTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.PassionTheme,
        darkTheme = SoulSearchingPalettes.darkPassion,
        lightTheme = SoulSearchingPalettes.lightPassion,
        name = CoreRes.string.passion_theme.toTextSpec()
    )

    private val greeneryTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.GreeneryTheme,
        darkTheme = SoulSearchingPalettes.darkGreenery,
        lightTheme = SoulSearchingPalettes.lightGreenery,
        name = CoreRes.string.greenery_theme.toTextSpec()
    )

    private val treeBarkTheme: SoulSearchingDarkLightTheme = SoulSearchingDarkLightTheme(
        id = SoulSearchingTheme.TreeBarkTheme,
        darkTheme = SoulSearchingPalettes.darkTreeBark,
        lightTheme = SoulSearchingPalettes.lightTreeBark,
        name = CoreRes.string.tree_bark_theme.toTextSpec()
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

