package com.github.enteraname74.soulsearching.feature.player.domain

import com.github.enteraname74.domain.model.Music
import java.util.*

sealed interface PlayerNavigationState {
    data object Idle : PlayerNavigationState
    data class ToModifyMusic(val music: Music) : PlayerNavigationState
    data class ToArtist(val artistId: UUID): PlayerNavigationState
    data class ToAlbum(val albumId: UUID): PlayerNavigationState
}