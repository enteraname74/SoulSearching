package com.github.soulsearching.events

import android.graphics.Bitmap
import com.github.soulsearching.classes.SortType
import com.github.soulsearching.database.model.ArtistWithMusics
import java.util.*

interface ArtistEvent {
    object UpdateArtist : ArtistEvent
    object DeleteArtist: ArtistEvent
    object SetDirectionSort : ArtistEvent
    data class SetSortType(val type: SortType) : ArtistEvent
    data class ArtistFromId(val artistId : UUID) : ArtistEvent
    data class ArtistWithMusicsFromId(val artistId : UUID) : ArtistEvent
    data class SetSelectedArtist(val artistWithMusics : ArtistWithMusics) : ArtistEvent
    data class SetName(val name: String) : ArtistEvent
    data class SetCover(val cover : Bitmap) : ArtistEvent
    data class BottomSheet(val isShown: Boolean) : ArtistEvent
    data class DeleteDialog(val isShown: Boolean) : ArtistEvent
}