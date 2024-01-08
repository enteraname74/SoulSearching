package com.github.soulsearching.viewmodel

import com.github.soulsearching.classes.utils.SharedPrefUtils

/**
 * View model for the settings.
 */
class SettingsViewModelImpl: SettingsViewModel() {
    /**
     * Update the type of color theme used in the application.
     */
    override fun updateColorTheme(newTheme: Int) {
        colorTheme = newTheme
        SharedPrefUtils.updateIntValue(
            keyToUpdate = SharedPrefUtils.COLOR_THEME_KEY,
            newValue = newTheme
        )
    }

    /**
     * Toggle dynamic theme for player.
     */
    override fun toggleDynamicPlayerTheme() {
        isDynamicPlayerThemeSelected = !isDynamicPlayerThemeSelected
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.DYNAMIC_PLAYER_THEME,
            newValue = isDynamicPlayerThemeSelected
        )
    }

    /**
     * Toggle dynamic theme for playlist.
     */
    override fun toggleDynamicPlaylistTheme() {
        isDynamicPlaylistThemeSelected = !isDynamicPlaylistThemeSelected
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.DYNAMIC_PLAYLIST_THEME,
            newValue = isDynamicPlaylistThemeSelected
        )
    }


    /**
     * Toggle dynamic theme for other views (everything except playlists and player view).
     */
    override fun toggleDynamicOtherViewsTheme() {
        isDynamicOtherViewsThemeSelected = !isDynamicOtherViewsThemeSelected
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.DYNAMIC_OTHER_VIEWS_THEME,
            newValue = isDynamicOtherViewsThemeSelected
        )
    }

    /**
     * Show or hide the quick access on the main page screen.
     */
    override fun toggleQuickAccessVisibility() {
        isQuickAccessShown = !isQuickAccessShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_QUICK_ACCESS_SHOWN,
            newValue = isQuickAccessShown
        )
    }

    /**
     * Show or hide the playlists on the main page screen.
     */
    override fun togglePlaylistsVisibility() {
        isPlaylistsShown = !isPlaylistsShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_PLAYLISTS_SHOWN,
            newValue = isPlaylistsShown
        )
    }

    /**
     * Show or hide the albums on the main page screen.
     */
    override fun toggleAlbumsVisibility() {
        isAlbumsShown = !isAlbumsShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_ALBUMS_SHOWN,
            newValue = isAlbumsShown
        )
    }

    /**
     * Show or hide the artists on the main page screen.
     */
    override fun toggleArtistsVisibility() {
        isArtistsShown = !isArtistsShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_ARTISTS_SHOWN,
            newValue = isArtistsShown
        )
    }

    /**
     * Activate or deactivate the vertical bar on the main page screen.
     */
    override fun toggleVerticalBarVisibility() {
        isVerticalBarShown = !isVerticalBarShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_VERTICAL_BAR_SHOWN,
            newValue = isVerticalBarShown
        )
    }
}