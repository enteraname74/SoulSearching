package com.github.enteraname74.soulsearching.feature.settings.statistics.model

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.soulsearching.coreui.strings.strings
import kotlin.uuid.Uuid

data class StatisticsUiElement(
    val id: Uuid,
    val title: String,
    val text: String,
    val onClick: (() -> Unit)?,
    val cover: Cover?,
)

fun ListeningStatistics.AlbumStats.toStatisticsUiElement(
    onClick: () -> Unit,
): StatisticsUiElement =
    StatisticsUiElement(
        id = id,
        title = album.name,
        text = strings.plays(nbPlayed),
        onClick = onClick,
        cover = album.cover,
    )

fun ListeningStatistics.ArtistStats.toStatisticsUiElement(
    onClick: () -> Unit,
): StatisticsUiElement =
    StatisticsUiElement(
        id = id,
        title = artist.name,
        text = strings.plays(nbPlayed),
        onClick = onClick,
        cover = artist.cover,
    )

fun ListeningStatistics.ArtistStats.toStatisticsUiMostSongsElement(
    onClick: () -> Unit,
): StatisticsUiElement =
    StatisticsUiElement(
        id = id,
        title = artist.name,
        text = strings.musics(artist.totalMusics),
        onClick = onClick,
        cover = artist.cover,
    )

fun ListeningStatistics.PlaylistStats.toStatisticsUiElement(
    onClick: () -> Unit,
): StatisticsUiElement =
    StatisticsUiElement(
        id = id,
        title = playlist.name,
        text = strings.plays(nbPlayed),
        onClick = onClick,
        cover = playlist.cover,
    )

fun ListeningStatistics.MusicStats.toStatisticsUiElement(): StatisticsUiElement =
    StatisticsUiElement(
        id = id,
        title = music.name,
        text = strings.plays(nbPlayed),
        onClick = null,
        cover = music.cover,
    )

fun ListeningStatistics.MusicStats.toStatisticsUiListenedElement(): StatisticsUiElement =
    StatisticsUiElement(
        id = id,
        title = music.name,
        text = strings.duration(timeListened),
        onClick = null,
        cover = music.cover,
    )