package com.github.soulsearching.events

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Album
import java.util.*

interface ArtistEvent {
    object UpdateArtist : ArtistEvent
    data class ArtistFromId(val artistId : UUID) : ArtistEvent
    data class DeleteArtist(val album: Album) : ArtistEvent
    data class ArtistWithMusicsFromId(val artistId : UUID) : ArtistEvent
    data class SetName(val name: String) : ArtistEvent
    data class SetCover(val cover : Bitmap) : ArtistEvent
}