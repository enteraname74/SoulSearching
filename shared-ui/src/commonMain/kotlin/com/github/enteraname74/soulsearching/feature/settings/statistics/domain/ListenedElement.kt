package com.github.enteraname74.soulsearching.feature.settings.statistics.domain

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import java.util.UUID

data class ListenedElement(
    val id: UUID,
    val title: String,
    val totalListened: Int,
    val coverId: UUID?
)

fun Music.toListenedElement(): ListenedElement = ListenedElement(
    title = name,
    totalListened = nbPlayed,
    coverId = coverId,
    id = musicId,
)

fun AlbumWithMusics.toListenedElement(): ListenedElement = ListenedElement(
    title = album.albumName,
    totalListened = album.nbPlayed,
    coverId = album.coverId,
    id = album.albumId,
)

fun ArtistWithMusics.toListenedElement(): ListenedElement = ListenedElement(
    title = artist.artistName,
    totalListened = artist.nbPlayed,
    coverId = artist.coverId,
    id = artist.artistId,
)

fun PlaylistWithMusics.toListenedElement(): ListenedElement = ListenedElement(
    title = playlist.name,
    totalListened = playlist.nbPlayed,
    coverId = playlist.coverId,
    id = playlist.playlistId,
)
