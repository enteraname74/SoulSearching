package com.github.soulsearching.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.soulsearching.classes.SharedPrefUtils
import com.github.soulsearching.classes.enumsAndTypes.ColorThemeType
import com.github.soulsearching.classes.enumsAndTypes.ElementEnum

class SettingsViewModel: ViewModel() {
    var colorTheme by mutableStateOf(ColorThemeType.SYSTEM)
    var isDynamicPlayerThemeSelected by mutableStateOf(false)
    var isDynamicPlaylistThemeSelected by mutableStateOf(false)

    var isQuickAccessShown by mutableStateOf(true)
    var isPlaylistsShown by mutableStateOf(true)
    var isAlbumsShown by mutableStateOf(true)
    var isArtistsShown by mutableStateOf(true)

    var isVerticalBarShown by mutableStateOf(false)

    fun updateColorTheme(newTheme: Int) {
        colorTheme = newTheme
        SharedPrefUtils.updateIntValue(
            keyToUpdate = SharedPrefUtils.COLOR_THEME_KEY,
            newValue = newTheme
        )
    }

    fun isPersonalizedDynamicPlayerThemeOn(): Boolean {
        return (colorTheme == ColorThemeType.PERSONALIZED && isDynamicPlayerThemeSelected) || colorTheme == ColorThemeType.DYNAMIC
    }

    fun isPersonalizedDynamicPlaylistThemeOn(): Boolean {
        return colorTheme == ColorThemeType.PERSONALIZED && isDynamicPlaylistThemeSelected
    }

    fun toggleDynamicPlayerTheme() {
        isDynamicPlayerThemeSelected = !isDynamicPlayerThemeSelected
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.DYNAMIC_PLAYER_THEME,
            newValue = isDynamicPlayerThemeSelected
        )
    }

    fun toggleDynamicPlaylistTheme() {
        isDynamicPlaylistThemeSelected = !isDynamicPlaylistThemeSelected
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.DYNAMIC_PLAYLIST_THEME,
            newValue = isDynamicPlaylistThemeSelected
        )
    }

    fun toggleQuickAccessVisibility() {
        isQuickAccessShown = !isQuickAccessShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_QUICK_ACCESS_SHOWN,
            newValue = isQuickAccessShown
        )
    }

    fun togglePlaylistsVisibility() {
        isPlaylistsShown = !isPlaylistsShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_PLAYLISTS_SHOWN,
            newValue = isPlaylistsShown
        )
    }

    fun toggleAlbumsVisibility() {
        isAlbumsShown = !isAlbumsShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_ALBUMS_SHOWN,
            newValue = isAlbumsShown
        )
    }

    fun toggleArtistsVisibility() {
        isArtistsShown = !isArtistsShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_ARTISTS_SHOWN,
            newValue = isArtistsShown
        )
    }

    fun toggleVerticalBarVisibility() {
        isVerticalBarShown = !isVerticalBarShown
        SharedPrefUtils.updateBooleanValue(
            keyToUpdate = SharedPrefUtils.IS_VERTICAL_BAR_SHOWN,
            newValue = isVerticalBarShown
        )
    }

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
}