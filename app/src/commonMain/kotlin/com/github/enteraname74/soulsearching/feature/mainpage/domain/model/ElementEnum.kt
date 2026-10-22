package com.github.enteraname74.soulsearching.feature.mainpage.domain.model

/**
 * Represent all elements that can be shown on the main page screen.
 */
enum class ElementEnum {
    QUICK_ACCESS,
    PLAYLISTS,
    COLLECTIONS,
    ALBUMS,
    ARTISTS,
    MUSICS,
    FOLDERS;

    companion object {
        fun fromRaw(value: String): ElementEnum? =
            runCatching {
                valueOf(value)
            }.getOrNull()
    }
}
