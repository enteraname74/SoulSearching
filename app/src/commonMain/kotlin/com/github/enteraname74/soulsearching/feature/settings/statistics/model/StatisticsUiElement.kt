package com.github.enteraname74.soulsearching.feature.settings.statistics.model

import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.statistics.ListeningStatistics
import kotlin.uuid.Uuid
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulTexts
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

data class StatisticsUiElement(
    val id: String,
    val title: SoulTextSpec,
    val text: SoulTextSpec,
    val onClick: (() -> Unit)?,
    val cover: Cover?,
)

fun ListeningStatistics.AlbumStats.toStatisticsUiElement(
    onClick: () -> Unit,
): StatisticsUiElement =
    StatisticsUiElement(
        id = id,
        title = album.name.toTextSpec(),
        text = SoulTexts.plays(nbPlayed),
        onClick = onClick,
        cover = album.cover,
    )

fun ListeningStatistics.ArtistStats.toStatisticsUiElement(
    onClick: () -> Unit,
): StatisticsUiElement =
    StatisticsUiElement(
        id = id,
        title = artist.name.toTextSpec(),
        text = SoulTexts.plays(nbPlayed),
        onClick = onClick,
        cover = artist.cover,
    )

fun ListeningStatistics.ArtistStats.toStatisticsUiMostSongsElement(
    onClick: () -> Unit,
): StatisticsUiElement =
    StatisticsUiElement(
        id = id,
        title = artist.name.toTextSpec(),
        text = SoulTexts.musics(artist.totalMusics),
        onClick = onClick,
        cover = artist.cover,
    )

fun ListeningStatistics.PlaylistStats.toStatisticsUiElement(
    onClick: () -> Unit,
): StatisticsUiElement =
    StatisticsUiElement(
        id = id,
        title = playlist.name.toTextSpec(),
        text = SoulTexts.plays(nbPlayed),
        onClick = onClick,
        cover = playlist.cover,
    )

fun ListeningStatistics.MusicStats.toStatisticsUiElement(): StatisticsUiElement =
    StatisticsUiElement(
        id = id,
        title = music.name.toTextSpec(),
        text = SoulTexts.plays(nbPlayed),
        onClick = null,
        cover = music.cover,
    )

fun ListeningStatistics.MusicStats.toStatisticsUiListenedElement(): StatisticsUiElement =
    StatisticsUiElement(
        id = id,
        title = music.name.toTextSpec(),
        text = SoulTexts.duration(timeListened),
        onClick = null,
        cover = music.cover,
    )