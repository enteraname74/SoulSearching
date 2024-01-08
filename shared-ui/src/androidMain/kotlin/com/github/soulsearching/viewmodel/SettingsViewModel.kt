package com.github.soulsearching.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.github.soulsearching.classes.types.ColorThemeType
import com.github.soulsearching.classes.types.ElementEnum
import com.github.soulsearching.classes.utils.SharedPrefUtils

/**
 * View model for the settings.
 */
class SettingsViewModel: ViewModel() {
    var colorTheme by mutableIntStateOf(ColorThemeType.SYSTEM)
    var isDynamicPlayerThemeSelected by mutableStateOf(false)
    var isDynamicPlaylistThemeSelected by mutableStateOf(false)
    var isDynamicOtherViewsThemeSelected by mutableStateOf(false)

    var isQuickAccessShown by mutableStateOf(true)
    var isPlaylistsShown by mutableStateOf(true)
    var isAlbumsShown by mutableStateOf(true)
    var isArtistsShown by mutableStateOf(true)

    var isVerticalBarShown by mutableStateOf(false)

    var playlistCover by mutableStateOf<ImageBitmap?>(null)
    var forceBasicThemeForPlaylists by mutableStateOf(false)

    /**
     * Update the type of color theme used in the application.
     */
    fun updateColorTheme(newTheme: Int) {
        colorTheme = newTheme
        SharedPrefUtils.updateIntValue(
            keyToUpdate = SharedPrefUtils.COLOR_THEME_KEY,
            newValue = newTheme
        )
    }

    /**
     * Check if the personalized dynamic playlist theme is off.
     * The conditions are :
     * - App color theme is on personalized
     * - The dynamic playlist theme is NOT selected
     */
    fun isPersonalizedDynamicPlaylistThemeOff(): Boolean {
        return colorTheme == ColorThemeType.PERSONALIZED && !isDynamicPlaylistThemeSelected
    }

    /**
     * Check if the personalized dynamic player theme is on.
     * The conditions are :
     * - App color theme is on personalized && The dynamic playlist theme is selected
     * - Or the color theme is on dynamic.
     */
    fun isPersonalizedDynamicPlayerThemeOn(): Boolean {
        return (colorTheme == ColorThemeType.PERSONALIZED && isDynamicPlayerThemeSelected) || colorTheme == ColorThemeType.DYNAMIC
    }

    /**
     * Check if the personalized dynamic playlist theme is on.
     * The conditions are :
     * - App color theme is on personalized
     * - The dynamic playlist theme is selected
     */
    fun isPersonalizedDynamicPlaylistThemeOn(): Boolean {
        return colorTheme == ColorThemeType.PERSONALIZED && isDynamicPlaylistThemeSelected
    }

    /**
     * Check if the dynamic theme is on.
     */
    fun isDynamicThemeOn(): Boolean {
        return colorTheme == ColorThemeType.DYNAMIC
    }


    /**
     * Check if the personalized dynamic other views theme is on.
     * The conditions are :
     * - App color theme is on personalized
     * - The dynamic theme for other views is selected
     */
    fun isPersonalizedDynamicOtherViewsThemeOn(): Boolean {
        return colorTheme == ColorThemeType.PERSONALIZED && isDynamicOtherViewsThemeSelected
    }

    /**
     * Toggle dynamic theme for player.
     */
    fun toggleDynamicPlayerTheme() {
        isDynamicPlayerThemeSelected = !isDynamicPlayerThemeSelected
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.DYNAMIC_PLAYER_THEME,
            newValue = isDynamicPlayerThemeSelected
        )
    }

    /**
     * Toggle dynamic theme for playlist.
     */
    fun toggleDynamicPlaylistTheme() {
        isDynamicPlaylistThemeSelected = !isDynamicPlaylistThemeSelected
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.DYNAMIC_PLAYLIST_THEME,
            newValue = isDynamicPlaylistThemeSelected
        )
    }


    /**
     * Toggle dynamic theme for other views (everything except playlists and player view).
     */
    fun toggleDynamicOtherViewsTheme() {
        isDynamicOtherViewsThemeSelected = !isDynamicOtherViewsThemeSelected
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.DYNAMIC_OTHER_VIEWS_THEME,
            newValue = isDynamicOtherViewsThemeSelected
        )
    }

    /**
     * Show or hide the quick access on the main page screen.
     */
    fun toggleQuickAccessVisibility() {
        isQuickAccessShown = !isQuickAccessShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_QUICK_ACCESS_SHOWN,
            newValue = isQuickAccessShown
        )
    }

    /**
     * Show or hide the playlists on the main page screen.
     */
    fun togglePlaylistsVisibility() {
        isPlaylistsShown = !isPlaylistsShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_PLAYLISTS_SHOWN,
            newValue = isPlaylistsShown
        )
    }

    /**
     * Show or hide the albums on the main page screen.
     */
    fun toggleAlbumsVisibility() {
        isAlbumsShown = !isAlbumsShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_ALBUMS_SHOWN,
            newValue = isAlbumsShown
        )
    }

    /**
     * Show or hide the artists on the main page screen.
     */
    fun toggleArtistsVisibility() {
        isArtistsShown = !isArtistsShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_ARTISTS_SHOWN,
            newValue = isArtistsShown
        )
    }

    /**
     * Activate or deactivate the vertical bar on the main page screen.
     */
    fun toggleVerticalBarVisibility() {
        isVerticalBarShown = !isVerticalBarShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_VERTICAL_BAR_SHOWN,
            newValue = isVerticalBarShown
        )
    }

    /**
     * Retrieve a list of visible elements on the main page screen.
     */
    fun getListOfVisibleElements(): ArrayList<ElementEnum> {
        val list: ArrayList<ElementEnum> = ArrayList()
        if (isQuickAccessShown) {
            list.add(ElementEnum.QUICK_ACCESS)
        }
        if (isPlaylistsShown) {
            list.add(ElementEnum.PLAYLISTS)
        }
        if (isAlbumsShown) {
            list.add(ElementEnum.ALBUMS)
        }
        if (isArtistsShown) {
            list.add(ElementEnum.ARTISTS)
        }
        list.add(ElementEnum.MUSICS)
        return list
    }

    /**
     * Define the current playlist cover.
     */
    fun setPlaylistCover(playlistImage: ImageBitmap?) {
        playlistCover = playlistImage
    }
}