package com.github.enteraname74.soulsearching.feature.settings.statistics.domain

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.coreui.strings.strings
import java.util.UUID

data class ListenedElement(
    val id: UUID,
    val title: String,
    val text: @Composable () -> String,
    val coverId: UUID?
)

fun Music.toListenedElement(): ListenedElement = ListenedElement(
    title = name,
    text = { strings.plays(nbPlayed) },
    coverId = coverId,
    id = musicId,
)

fun AlbumWithMusics.toListenedElement(): ListenedElement = ListenedElement(
    title = album.albumName,
    text = { strings.plays(album.nbPlayed) },
    coverId = album.coverId,
    id = album.albumId,
)

fun ArtistWithMusics.toListenedElement(): ListenedElement = ListenedElement(
    title = artist.artistName,
    text = { strings.plays(artist.nbPlayed) },
    coverId = artist.coverId,
    id = artist.artistId,
)

fun ArtistWithMusics.toMostSongsListenedElement(): ListenedElement = ListenedElement(
    title = artist.artistName,
    text = { strings.musics(musics.size) },
    coverId = artist.coverId,
    id = artist.artistId,
)

fun PlaylistWithMusics.toListenedElement(): ListenedElement = ListenedElement(
    title = playlist.name,
    text = { strings.plays(playlist.nbPlayed) },
    coverId = playlist.coverId,
    id = playlist.playlistId,
)
