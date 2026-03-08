package com.github.enteraname74.soulsearching.feature.settings.statistics.domain

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistPreview
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

fun AlbumPreview.toListenedElement(): ListenedElement = ListenedElement(
    title = name,
    text = { strings.plays(nbPlayed) },
    cover = cover,
    id = id,
)

fun ArtistPreview.toListenedElement(): ListenedElement = ListenedElement(
    title = name,
    text = { strings.plays(totalMusics) },
    cover = cover,
    id = id,
)

fun ArtistPreview.toMostSongsListenedElement(): ListenedElement = ListenedElement(
    title = name,
    text = { strings.musics(totalMusics) },
    cover = cover,
    id = id,
)

fun PlaylistPreview.toListenedElement(): ListenedElement = ListenedElement(
    title = name,
    text = { strings.plays(nbPlayed) },
    cover = cover,
    id = id,
)
