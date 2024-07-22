package com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain

import java.util.*

/**
 * Events of the selected artist screen.
 */
sealed interface SelectedArtistEvent {
    data class SetSelectedArtist(val artistId: UUID): SelectedArtistEvent
    data class AddNbPlayed(val artistId: UUID): SelectedArtistEvent
}
