package com.github.soulsearching.events

import android.graphics.Bitmap
import com.github.soulsearching.database.model.Album
import java.util.UUID

interface AlbumEvent {
    object UpdateAlbum : AlbumEvent
    data class AlbumFromID(val albumId : UUID) : AlbumEvent
    data class DeleteAlbum(val album: Album) : AlbumEvent
    data class AddAlbum(val album: Album) : AlbumEvent
    data class SetName(val name: String) : AlbumEvent
    data class SetArtist(val name: String) : AlbumEvent
    data class SetCover(val cover : Bitmap) : AlbumEvent
}