package com.github.enteraname74.soulsearching.domain.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
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
    var isPlaylistsShown by mutableStateOf(true)
        private set
    var isAlbumsShown by mutableStateOf(true)
        private set
    var isArtistsShown by mutableStateOf(true)
        private set

    var isMusicFileModificationOn by mutableStateOf(true)
        private set

    var areMusicsByFoldersShown by mutableStateOf(false)
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
    ) { isQuickAccessShown, arePlaylistsShown, areAlbumsShown, areArtistsShown ->
        ElementsVisibility(
            isQuickAccessShown = isQuickAccessShown,
            arePlaylistsShown = arePlaylistsShown,
            areAlbumsShown = areAlbumsShown,
            areArtistsShown = areArtistsShown,
        )
    }

    /**
     * Initialize the manager.
     */
    /**
     * Initialize the manager.
     */
    private fun initializeManager() {
        with(settings) {
            isQuickAccessShown = getBoolean(
                SoulSearchingSettings.IS_QUICK_ACCESS_SHOWN, true
            )
            isPlaylistsShown = getBoolean(
                SoulSearchingSettings.IS_PLAYLISTS_SHOWN, true
            )
            isAlbumsShown = getBoolean(
                SoulSearchingSettings.IS_ALBUMS_SHOWN, true
            )
            isArtistsShown = getBoolean(
                SoulSearchingSettings.IS_ARTISTS_SHOWN, true
            )
            isMusicFileModificationOn = getBoolean(
                SoulSearchingSettings.IS_MUSIC_FILE_MODIFICATION_ON,
                true
            )
            areMusicsByMonthsShown = getBoolean(
                SoulSearchingSettings.ARE_MUSICS_BY_MONTHS_SHOWN,
                false
            )
            areMusicsByFoldersShown = getBoolean(
                SoulSearchingSettings.ARE_MUSICS_BY_FOLDERS_SHOWN,
                false
            )
        }
    }

    /**
     * Show or hide the quick access on the main page screen.
     */
    /**
     * Show or hide the quick access on the main page screen.
     */
    fun toggleQuickAccessVisibility() {
        isQuickAccessShown = !isQuickAccessShown
        settings.setBoolean(
            key = SoulSearchingSettings.IS_QUICK_ACCESS_SHOWN,
            value = isQuickAccessShown
        )
    }

    /**
     * Show or hide the playlists on the main page screen.
     */
    /**
     * Show or hide the playlists on the main page screen.
     */
    fun togglePlaylistsVisibility() {
        isPlaylistsShown = !isPlaylistsShown
        settings.setBoolean(
            key = SoulSearchingSettings.IS_PLAYLISTS_SHOWN,
            value = isPlaylistsShown
        )
    }

    /**
     * Show or hide the albums on the main page screen.
     */
    /**
     * Show or hide the albums on the main page screen.
     */
    fun toggleAlbumsVisibility() {
        isAlbumsShown = !isAlbumsShown
        settings.setBoolean(
            key = SoulSearchingSettings.IS_ALBUMS_SHOWN,
            value = isAlbumsShown
        )
    }

    /**
     * Show or hide the artists on the main page screen.
     */
    /**
     * Show or hide the artists on the main page screen.
     */
    fun toggleArtistsVisibility() {
        isArtistsShown = !isArtistsShown
        settings.setBoolean(
            key = SoulSearchingSettings.IS_ARTISTS_SHOWN,
            value = isArtistsShown
        )
    }

    /**
     * Active or deactivate the music file modification.
     */
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

    /**
     * Show or hide the musics by folders part in the musics tab.
     */
    /**
     * Show or hide the musics by folders part in the musics tab.
     */
    fun toggleMusicsByFoldersVisibility() {
        areMusicsByFoldersShown = !areMusicsByFoldersShown
        settings.setBoolean(
            key = SoulSearchingSettings.ARE_MUSICS_BY_FOLDERS_SHOWN,
            value = areMusicsByFoldersShown
        )
    }

    /**
     * Enable or disable the swipe to change current song on player view.
     */
    /**
     * Enable or disable the swipe to change current song on player view.
     */
    fun togglePlayerSwipe() {
        isPlayerSwipeEnabled = !isPlayerSwipeEnabled
        settings.setBoolean(
            key = SoulSearchingSettings.IS_PLAYER_SWIPE_ENABLED,
            value = isPlayerSwipeEnabled
        )
    }
}