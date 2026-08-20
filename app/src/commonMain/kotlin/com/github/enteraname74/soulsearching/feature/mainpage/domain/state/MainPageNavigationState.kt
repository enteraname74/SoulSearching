package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedScreenFocusedElement
import kotlin.uuid.Uuid

sealed interface MainPageNavigationState {
    data object Idle: MainPageNavigationState
    data class ToModifyMusic(val musicId: Uuid): MainPageNavigationState
    data class ToModifyAlbum(val albumId: Uuid): MainPageNavigationState
    data class ToModifyArtist(val artistId: Uuid): MainPageNavigationState
    data class ToModifyPlaylist(val playlistId: Uuid): MainPageNavigationState

    data class ToPlaylist(val playlistId: Uuid): MainPageNavigationState
    data class ToAlbum(val albumId: Uuid): MainPageNavigationState
    data class ToArtist(val artistId: Uuid): MainPageNavigationState
    data class ToMonth(val month: String): MainPageNavigationState
    data class ToFolder(val folderPath: String): MainPageNavigationState
    data object ToSettings: MainPageNavigationState
    data class ToMusicBottomSheet(val musicIds: List<Uuid>) : MainPageNavigationState
    data class ToPlaylistBottomSheet(val playlistIds: List<Uuid>) : MainPageNavigationState
    data class ToArtistBottomSheet(val artistIds: List<Uuid>) : MainPageNavigationState
    data class ToAlbumBottomSheet(val albumIds: List<Uuid>) : MainPageNavigationState

    data class ToAdvancedSettings(
        val focusedElement: SettingsAdvancedScreenFocusedElement?,
    ): MainPageNavigationState
}
