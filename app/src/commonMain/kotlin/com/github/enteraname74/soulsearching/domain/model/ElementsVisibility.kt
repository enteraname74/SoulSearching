package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum

data class ElementsVisibility(
    val isQuickAccessShown: Boolean,
    val arePlaylistsShown: Boolean,
    val areAlbumsShown: Boolean,
    val areArtistsShown: Boolean,
    val areMusicFoldersShown: Boolean,
) {
    fun toElementEnums(): List<ElementEnum> =
        buildList {
            if (isQuickAccessShown) add(ElementEnum.QUICK_ACCESS)
            add(ElementEnum.MUSICS)
            if (arePlaylistsShown) add(ElementEnum.PLAYLISTS)
            if (areAlbumsShown) add(ElementEnum.ALBUMS)
            if (areArtistsShown) add(ElementEnum.ARTISTS)
            if (areMusicFoldersShown) add(ElementEnum.FOLDERS)
        }
}
