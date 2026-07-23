package com.github.enteraname74.soulsearching.feature.player.domain.state

import com.github.enteraname74.domain.model.Music
import kotlin.uuid.Uuid

sealed interface PlayerNavigationState {
    data object Idle : PlayerNavigationState
    data class ToModifyMusic(val music: Music) : PlayerNavigationState
    data class ToArtist(val artistId: Uuid): PlayerNavigationState
    data class ToAlbum(val albumId: Uuid): PlayerNavigationState
    data object ToRemoteLyricsSettings : PlayerNavigationState
    data class ToMusicBottomSheet(val musicIds: List<Uuid>) : PlayerNavigationState
}
