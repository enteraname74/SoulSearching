package com.github.soulsearching.events

import android.graphics.Bitmap
import com.github.soulsearching.classes.SortType
import com.github.soulsearching.database.model.AlbumWithArtist
import java.util.*

interface AlbumEvent {
    object UpdateAlbum : AlbumEvent
    data class AlbumFromID(val albumId: UUID) : AlbumEvent
    object DeleteAlbum : AlbumEvent
    object SetDirectionSort : AlbumEvent
    data class SetSortType(val type: SortType) : AlbumEvent
    data class AlbumWithMusicsFromId(val albumId: UUID) : AlbumEvent
    data class SetSelectedAlbum(val albumWithArtist: AlbumWithArtist): AlbumEvent
    data class SetName(val name: String) : AlbumEvent
    data class SetArtist(val artist: String) : AlbumEvent
    data class SetCover(val cover: Bitmap) : AlbumEvent
    data class BottomSheet(val isShown: Boolean) : AlbumEvent
    data class DeleteDialog(val isShown: Boolean) : AlbumEvent
}