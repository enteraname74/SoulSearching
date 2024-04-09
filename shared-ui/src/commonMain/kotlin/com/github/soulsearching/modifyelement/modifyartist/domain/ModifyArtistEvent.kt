package com.github.soulsearching.modifyelement.modifyartist.domain

import androidx.compose.ui.graphics.ImageBitmap
import java.util.UUID

/**
 * Events for the modify artist screen.
 */
sealed interface ModifyArtistEvent {
    data object UpdateArtist : ModifyArtistEvent
    data class ArtistFromId(val artistId : UUID) : ModifyArtistEvent
    data class SetName(val name: String) : ModifyArtistEvent
    data class SetCover(val cover : ImageBitmap) : ModifyArtistEvent
}