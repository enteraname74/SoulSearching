package com.github.soulsearching.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.types.ColorThemeType
import com.github.soulsearching.types.ElementEnum

open class SettingsViewModel(
    private val settings: SoulSearchingSettings
) {
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

    init {
        initializeViewModel()
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
     * Initialize the view model.
     */
    private fun initializeViewModel() {
        with(settings) {
            colorTheme = getInt(
                SoulSearchingSettings.COLOR_THEME_KEY, ColorThemeType.DYNAMIC
            )
            isDynamicPlayerThemeSelected = settings.getBoolean(
                SoulSearchingSettings.DYNAMIC_PLAYER_THEME, false
            )
            isDynamicPlaylistThemeSelected = settings.getBoolean(
                SoulSearchingSettings.DYNAMIC_PLAYLIST_THEME, false
            )
            isQuickAccessShown = settings.getBoolean(
                SoulSearchingSettings.IS_QUICK_ACCESS_SHOWN, true
            )
            isPlaylistsShown = settings.getBoolean(
                SoulSearchingSettings.IS_PLAYLISTS_SHOWN, true
            )
            isAlbumsShown = settings.getBoolean(
                SoulSearchingSettings.IS_ALBUMS_SHOWN, true
            )
            isArtistsShown = settings.getBoolean(
                SoulSearchingSettings.IS_ARTISTS_SHOWN, true
            )
            isVerticalBarShown = settings.getBoolean(
                SoulSearchingSettings.IS_VERTICAL_BAR_SHOWN, false
            )
        }
    }

    /**
     * Update the type of color theme used in the application.
     */
    fun updateColorTheme(newTheme: Int) {
        colorTheme = newTheme
        settings.setInt(
            key = SoulSearchingSettings.COLOR_THEME_KEY,
            value = newTheme
        )
    }

    /**
     * Toggle dynamic theme for player.
     */
    fun toggleDynamicPlayerTheme() {
        isDynamicPlayerThemeSelected = !isDynamicPlayerThemeSelected
        settings.setBoolean(
            key = SoulSearchingSettings.DYNAMIC_PLAYER_THEME,
            value = isDynamicPlayerThemeSelected
        )
    }

    /**
     * Toggle dynamic theme for playlist.
     */
    fun toggleDynamicPlaylistTheme() {
        isDynamicPlaylistThemeSelected = !isDynamicPlaylistThemeSelected
        settings.setBoolean(
            key = SoulSearchingSettings.DYNAMIC_PLAYLIST_THEME,
            value = isDynamicPlaylistThemeSelected
        )
    }

    /**
     * Toggle dynamic theme for other views (everything except playlists and player view).
     */
    fun toggleDynamicOtherViewsTheme() {
        isDynamicOtherViewsThemeSelected = !isDynamicOtherViewsThemeSelected
        settings.setBoolean(
            key = SoulSearchingSettings.DYNAMIC_OTHER_VIEWS_THEME,
            value = isDynamicOtherViewsThemeSelected
        )
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