package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedScreenFocusedElement
import java.util.*

sealed interface MainPageNavigationState {
    data object Idle: MainPageNavigationState
    data class ToModifyMusic(val musicId: UUID): MainPageNavigationState
    data class ToModifyAlbum(val albumId: UUID): MainPageNavigationState
    data class ToModifyArtist(val artistId: UUID): MainPageNavigationState
    data class ToModifyPlaylist(val playlistId: UUID): MainPageNavigationState

    data class ToPlaylist(val playlistId: UUID): MainPageNavigationState
    data class ToAlbum(val albumId: UUID): MainPageNavigationState
    data class ToArtist(val artistId: UUID): MainPageNavigationState
    data class ToMonth(val month: String): MainPageNavigationState
    data class ToFolder(val folderPath: String): MainPageNavigationState

    data class ToAdvancedSettings(
        val focusedElement: SettingsAdvancedScreenFocusedElement?,
    ): MainPageNavigationState
}