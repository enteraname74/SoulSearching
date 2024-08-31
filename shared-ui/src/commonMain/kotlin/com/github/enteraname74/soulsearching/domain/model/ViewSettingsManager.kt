package com.github.enteraname74.soulsearching.domain.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Manage settings of the application linked to the view.
 */
class ViewSettingsManager(
    private val settings: SoulSearchingSettings
) {
    var isQuickAccessShown by mutableStateOf(true)
        private set

    var isMusicFileModificationOn by mutableStateOf(true)
        private set

    var areMusicsByMonthsShown by mutableStateOf(false)
        private set

    var isPlayerSwipeEnabled by mutableStateOf(true)
        private set

    init {
        initializeManager()
    }

    val visibleElements: Flow<ElementsVisibility> = combine(
        settings.getFlowOn(SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN),
        settings.getFlowOn(SoulSearchingSettingsKeys.MainPage.IS_PLAYLISTS_SHOWN),
        settings.getFlowOn(SoulSearchingSettingsKeys.MainPage.IS_ALBUMS_SHOWN),
        settings.getFlowOn(SoulSearchingSettingsKeys.MainPage.IS_ARTISTS_SHOWN),
        settings.getFlowOn(SoulSearchingSettingsKeys.MainPage.ARE_MUSICS_BY_FOLDERS_SHOWN),
    ) { isQuickAccessShown, arePlaylistsShown, areAlbumsShown, areArtistsShown, areMusicFoldersShown ->
        ElementsVisibility(
            isQuickAccessShown = isQuickAccessShown,
            arePlaylistsShown = arePlaylistsShown,
            areAlbumsShown = areAlbumsShown,
            areArtistsShown = areArtistsShown,
            areMusicFoldersShown = areMusicFoldersShown,
        )
    }

    /**
     * Initialize the manager.
     */
    private fun initializeManager() {
        with(settings) {
            isQuickAccessShown = get(SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN)
            isMusicFileModificationOn = get(SoulSearchingSettingsKeys.IS_MUSIC_FILE_MODIFICATION_ON)
            areMusicsByMonthsShown = get(SoulSearchingSettingsKeys.MainPage.ARE_MUSICS_BY_MONTHS_SHOWN)
        }
    }

    /**
     * Active or deactivate the music file modification.
     */
    fun toggleMusicFileModification() {
        isMusicFileModificationOn = !isMusicFileModificationOn
        settings.set(
            key = SoulSearchingSettingsKeys.IS_MUSIC_FILE_MODIFICATION_ON.key,
            value = isMusicFileModificationOn
        )
    }

    /**
     * Show or hide the musics by months part in the musics tab.
     */
    fun toggleMusicsByMonthsVisibility() {
        areMusicsByMonthsShown = !areMusicsByMonthsShown
        settings.set(
            key = SoulSearchingSettingsKeys.MainPage.ARE_MUSICS_BY_MONTHS_SHOWN.key,
            value = areMusicsByMonthsShown
        )
    }
}