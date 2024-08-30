package com.github.enteraname74.soulsearching.domain.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
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
        settings.getFlowOn(SoulSearchingSettings.IS_QUICK_ACCESS_SHOWN, true),
        settings.getFlowOn(SoulSearchingSettings.IS_PLAYLISTS_SHOWN, true),
        settings.getFlowOn(SoulSearchingSettings.IS_ALBUMS_SHOWN, true),
        settings.getFlowOn(SoulSearchingSettings.IS_ARTISTS_SHOWN, true),
        settings.getFlowOn(SoulSearchingSettings.ARE_MUSICS_BY_FOLDERS_SHOWN, true),
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
            isQuickAccessShown = getBoolean(
                SoulSearchingSettings.IS_QUICK_ACCESS_SHOWN, true
            )
            isMusicFileModificationOn = getBoolean(
                SoulSearchingSettings.IS_MUSIC_FILE_MODIFICATION_ON,
                true
            )
            areMusicsByMonthsShown = getBoolean(
                SoulSearchingSettings.ARE_MUSICS_BY_MONTHS_SHOWN,
                false
            )
        }
    }

    /**
     * Active or deactivate the music file modification.
     */
    fun toggleMusicFileModification() {
        isMusicFileModificationOn = !isMusicFileModificationOn
        settings.setBoolean(
            key = SoulSearchingSettings.IS_MUSIC_FILE_MODIFICATION_ON,
            value = isMusicFileModificationOn
        )
    }

    /**
     * Show or hide the musics by months part in the musics tab.
     */
    fun toggleMusicsByMonthsVisibility() {
        areMusicsByMonthsShown = !areMusicsByMonthsShown
        settings.setBoolean(
            key = SoulSearchingSettings.ARE_MUSICS_BY_MONTHS_SHOWN,
            value = areMusicsByMonthsShown
        )
    }
}