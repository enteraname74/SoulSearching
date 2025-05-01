package com.github.enteraname74.soulsearching.feature.settings.statistics.domain

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.coreui.strings.strings
import java.util.UUID

data class ListenedElement(
    val id: UUID,
    val title: String,
    val text: @Composable () -> String,
    val cover: Cover?,
)

fun Music.toListenedElement(): ListenedElement = ListenedElement(
    title = name,
    text = { strings.plays(nbPlayed) },
    cover = cover,
    id = musicId,
)

fun AlbumWithMusics.toListenedElement(): ListenedElement = ListenedElement(
    title = album.albumName,
    text = { strings.plays(album.nbPlayed) },
    cover = cover,
    id = album.albumId,
)

fun ArtistWithMusics.toListenedElement(): ListenedElement = ListenedElement(
    title = artist.artistName,
    text = { strings.plays(artist.nbPlayed) },
    cover = cover,
    id = artist.artistId,
)

fun ArtistWithMusics.toMostSongsListenedElement(): ListenedElement = ListenedElement(
    title = artist.artistName,
    text = { strings.musics(musics.size) },
    cover = cover,
    id = artist.artistId,
)

fun PlaylistWithMusics.toListenedElement(): ListenedElement = ListenedElement(
    title = playlist.name,
    text = { strings.plays(playlist.nbPlayed) },
    cover = cover,
    id = playlist.playlistId,
)
