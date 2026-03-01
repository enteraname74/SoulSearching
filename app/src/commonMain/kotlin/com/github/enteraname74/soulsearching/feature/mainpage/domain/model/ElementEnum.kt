package com.github.enteraname74.soulsearching.feature.mainpage.domain.model

import com.github.enteraname74.domain.model.settings.settingElementOf

/**
 * Represent all elements that can be shown on the main page screen.
 */
enum class ElementEnum {
    QUICK_ACCESS,
    PLAYLISTS,
    ALBUMS,
    ARTISTS,
    MUSICS,
    FOLDERS;

    companion object {
        val INITIAL_TAB_SETTINGS_KEY = settingElementOf(
            key = "MAIN_PAGE_INITAL_TAB",
            defaultValue = QUICK_ACCESS.name,
        )

        fun fromRaw(value: String): ElementEnum? =
            runCatching {
                valueOf(value)
            }.getOrNull()
    }
}