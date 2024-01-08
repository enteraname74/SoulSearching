package com.github.soulsearching.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.github.soulsearching.classes.types.ColorThemeType
import com.github.soulsearching.classes.types.ElementEnum

abstract class SettingsViewModel {
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
    fun setNewPlaylistCover(playlistImage: ImageBitmap?) {
        playlistCover = playlistImage
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
     * Update the type of color theme used in the application.
     */
    abstract fun updateColorTheme(newTheme: Int)

    /**
     * Toggle dynamic theme for player.
     */
    abstract fun toggleDynamicPlayerTheme()

    /**
     * Toggle dynamic theme for playlist.
     */
    abstract fun toggleDynamicPlaylistTheme()

    /**
     * Toggle dynamic theme for other views (everything except playlists and player view).
     */
    abstract fun toggleDynamicOtherViewsTheme()

    /**
     * Show or hide the quick access on the main page screen.
     */
    abstract fun toggleQuickAccessVisibility()

    /**
     * Show or hide the playlists on the main page screen.
     */
    abstract fun togglePlaylistsVisibility()

    /**
     * Show or hide the albums on the main page screen.
     */
    abstract fun toggleAlbumsVisibility()

    /**
     * Show or hide the artists on the main page screen.
     */
    abstract fun toggleArtistsVisibility()

    /**
     * Activate or deactivate the vertical bar on the main page screen.
     */
    abstract fun toggleVerticalBarVisibility()
}