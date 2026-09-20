package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed interface MusicListDetailId {
    val id: String

    @Serializable
    data class Artist(val artistId: Uuid) : MusicListDetailId {
        override val id: String = artistId.toString()
    }

    @Serializable
    data class Album(val albumId: Uuid) : MusicListDetailId {
        override val id: String = albumId.toString()
    }

    @Serializable
    data class Month(val month: String) : MusicListDetailId {
        override val id: String = month
    }

    @Serializable
    data class Folder(val folder: String) : MusicListDetailId {
        override val id: String = folder
    }

    @Serializable
    data class Playlist(val playlistId: Uuid) : MusicListDetailId {
        override val id: String = playlistId.toString()
    }
}