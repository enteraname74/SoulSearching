package com.github.soulsearching.events

import com.github.soulsearching.database.model.Album

interface AlbumEvent {
    object ModifyAlbum : AlbumEvent
    data class DeleteAlbum(val album: Album) : AlbumEvent
    data class AddAlbum(val album: Album) : AlbumEvent
}