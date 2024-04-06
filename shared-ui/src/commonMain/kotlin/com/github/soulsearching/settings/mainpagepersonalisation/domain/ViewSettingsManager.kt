package com.github.soulsearching.settings.mainpagepersonalisation.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.soulsearching.mainpage.domain.model.ElementEnum
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings

/**
 * Manage settings of the application linked to the view.
 */
class ViewSettingsManager(
    private val settings: SoulSearchingSettings
) {
    var isQuickAccessShown by mutableStateOf(true)
    var isPlaylistsShown by mutableStateOf(true)
    var isAlbumsShown by mutableStateOf(true)
    var isArtistsShown by mutableStateOf(true)

    var isVerticalBarShown by mutableStateOf(false)

    init {
        initializeManager()
    }

    /**
     * Retrieve a list of visible elements on the main page screen.
     */
    fun getListOfVisibleElements(): List<ElementEnum> {
        val list: ArrayList<ElementEnum> = ArrayList()
        if (isQuickAccessShown) {
            list.add(ElementEnum.QUICK_ACCESS)
        }
        list.add(ElementEnum.MUSICS)
        if (isPlaylistsShown) {
            list.add(ElementEnum.PLAYLISTS)
        }
        if (isAlbumsShown) {
            list.add(ElementEnum.ALBUMS)
        }
        if (isArtistsShown) {
            list.add(ElementEnum.ARTISTS)
        }
        return list
    }

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
            isVerticalBarShown = getBoolean(
                SoulSearchingSettings.IS_VERTICAL_BAR_SHOWN, false
            )
        }
    }

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
    fun toggleArtistsVisibility() {
        isArtistsShown = !isArtistsShown
        settings.setBoolean(
            key = SoulSearchingSettings.IS_ARTISTS_SHOWN,
            value = isArtistsShown
        )
    }

    /**
     * Activate or deactivate the vertical bar on the main page screen.
     */
    fun toggleVerticalBarVisibility() {
        isVerticalBarShown = !isVerticalBarShown
        settings.setBoolean(
            key = SoulSearchingSettings.IS_VERTICAL_BAR_SHOWN,
            value = isVerticalBarShown
        )
    }
}