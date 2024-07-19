package com.github.enteraname74.soulsearching.domain.events

import com.github.enteraname74.domain.model.ArtistWithMusics
import java.util.UUID

/**
 * Events related to artists.
 */
sealed interface ArtistEvent {
    data class SetSortDirection(val type: Int) : ArtistEvent
    data class SetSortType(val type: Int) : ArtistEvent
    data class AddNbPlayed(val artistId: UUID): ArtistEvent

}