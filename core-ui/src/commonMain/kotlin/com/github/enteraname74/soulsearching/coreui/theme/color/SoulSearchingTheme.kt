package com.github.enteraname74.soulsearching.coreui.theme.color

enum class SoulSearchingTheme(val value: String) {
    MainTheme("SS_MAIN_THEME"),
    SteelTheme("SS_STEEL_THEME"),
    GlacierTheme("SS_GLACIER_THEME"),
    DuskTheme("SS_DUSK_THEME"),
    PassionTheme("SS_PASSION_THEME"),
    GreeneryTheme("SS_GREENERY_THEME"),
    TreeBarkTheme("SS_TREE_BARK_THEME"),
    ;

    companion object {
        fun from(id: String): SoulSearchingTheme =
            entries.find { it.value == id } ?: MainTheme
    }
}