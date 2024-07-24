package com.github.enteraname74.soulsearching.feature.elementpage.albumpage.domain

import com.github.enteraname74.domain.model.Music
import java.util.UUID

/**
 * Events for the selected album screen.
 */
sealed interface SelectedAlbumEvent {
    data class SetSelectedAlbum(val albumId: UUID): SelectedAlbumEvent
    data class AddNbPlayed(val albumId: UUID): SelectedAlbumEvent
}