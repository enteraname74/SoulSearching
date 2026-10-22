package com.github.enteraname74.soulsearching.domain.model.settings

import kotlinx.serialization.Serializable

@Serializable
enum class SoulSearchingTheme(val value: String) {
    MainTheme("SS_MAIN_THEME"), SteelTheme("SS_STEEL_THEME"), GlacierTheme("SS_GLACIER_THEME"),
    DuskTheme("SS_DUSK_THEME"), PassionTheme("SS_PASSION_THEME"), GreeneryTheme("SS_GREENERY_THEME"),
    TreeBarkTheme("SS_TREE_BARK_THEME");

    companion object {
        fun fromOrDefault(value: String): SoulSearchingTheme = entries.firstOrNull { it.value == value } ?: MainTheme
        fun from(id: String): SoulSearchingTheme = fromOrDefault(id)
    }
}
