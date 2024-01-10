package com.github.soulsearching.viewmodel.handler

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.theme.ColorThemeManager
import com.github.soulsearching.types.ElementEnum

/**
 * Handler for managing the SelectedViewModel.
 */
class SettingsViewModelHandler(
    private val settings: SoulSearchingSettings,
    private val colorThemeManager: ColorThemeManager
) : ViewModelHandler {
    var isQuickAccessShown by mutableStateOf(true)
    var isPlaylistsShown by mutableStateOf(true)
    var isAlbumsShown by mutableStateOf(true)
    var isArtistsShown by mutableStateOf(true)

    var isVerticalBarShown by mutableStateOf(false)

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
        colorThemeManager.playlistCover = playlistImage
    }

    /**
     * Check if the personalized dynamic playlist theme is off.
     * The conditions are :
     * - App color theme is on personalized
     * - The dynamic playlist theme is NOT selected
     */
    fun isPersonalizedDynamicPlaylistThemeOff(): Boolean {
        return colorThemeManager.isPersonalizedDynamicPlaylistThemeOff()
    }

    /**
     * Check if the personalized dynamic player theme is on.
     * The conditions are :
     * - App color theme is on personalized && The dynamic playlist theme is selected
     * - Or the color theme is on dynamic.
     */
    fun isPersonalizedDynamicPlayerThemeOn(): Boolean {
        return colorThemeManager.isPersonalizedDynamicPlayerThemeOn()
    }

    /**
     * Check if the personalized dynamic playlist theme is on.
     * The conditions are :
     * - App color theme is on personalized
     * - The dynamic playlist theme is selected
     */
    fun isPersonalizedDynamicPlaylistThemeOn(): Boolean {
        return colorThemeManager.isPersonalizedDynamicPlaylistThemeOn()
    }

    /**
     * Check if the personalized dynamic other views theme is on.
     * The conditions are :
     * - App color theme is on personalized
     * - The dynamic theme for other views is selected
     */
    fun isPersonalizedDynamicOtherViewsThemeOn(): Boolean {
        return colorThemeManager.isPersonalizedDynamicOtherViewsThemeOn()
    }

    /**
     * Initialize the view model.
     */
    private fun initializeViewModel() {
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
     * Update the type of color theme used in the application.
     */
    fun updateColorTheme(newTheme: Int) {
        colorThemeManager.colorThemeType = newTheme
        settings.setInt(
            key = SoulSearchingSettings.COLOR_THEME_KEY,
            value = newTheme
        )
    }

    /**
     * Toggle dynamic theme for player.
     */
    fun toggleDynamicPlayerTheme() {
        colorThemeManager.isDynamicPlayerThemeSelected = !colorThemeManager.isDynamicPlayerThemeSelected
        settings.setBoolean(
            key = SoulSearchingSettings.DYNAMIC_PLAYER_THEME,
            value = colorThemeManager.isDynamicPlayerThemeSelected
        )
    }

    /**
     * Toggle dynamic theme for playlist.
     */
    fun toggleDynamicPlaylistTheme() {
        colorThemeManager.isDynamicPlaylistThemeSelected = !colorThemeManager.isDynamicPlaylistThemeSelected
        settings.setBoolean(
            key = SoulSearchingSettings.DYNAMIC_PLAYLIST_THEME,
            value = colorThemeManager.isDynamicPlaylistThemeSelected
        )
    }

    /**
     * Toggle dynamic theme for other views (everything except playlists and player view).
     */
    fun toggleDynamicOtherViewsTheme() {
        colorThemeManager.isDynamicOtherViewsThemeSelected = !colorThemeManager.isDynamicOtherViewsThemeSelected
        settings.setBoolean(
            key = SoulSearchingSettings.DYNAMIC_OTHER_VIEWS_THEME,
            value = colorThemeManager.isDynamicOtherViewsThemeSelected
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