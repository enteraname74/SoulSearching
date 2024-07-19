package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.Artist

sealed interface AllArtistsNavigationState {
    data object Idle: AllArtistsNavigationState
    data class ToModifyArtist(val selectedArtist: Artist): AllArtistsNavigationState
}