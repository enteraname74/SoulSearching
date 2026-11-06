package com.github.enteraname74.soulsearching.feature.settings.statistics.model

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.most_listened_song
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.most_played_song
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.statistics_listening_time_label
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.statistics_listening_time_subtitle
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.statistics_songs_played_label
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.statistics_songs_played_subtitle
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.statistics_unique_artists_label
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.statistics_unique_artists_subtitle
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.statistics_unique_songs_label
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.statistics_unique_songs_subtitle
import com.github.enteraname74.soulsearching.coreui.strings.SoulTexts
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.soulsearching.domain.model.statistics.PeriodGeneralStatistics
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.cards.SettingsStatisticsMostPlayedSongSpec
import com.github.enteraname74.soulsearching.feature.settings.statistics.composable.cards.SettingsStatisticsSmallCardSpec
import kotlin.time.Duration

data class PeriodGeneralUiStatistics(
    val musicStatistics: MusicStatistics?,
    val totalMusicsPlayed: SettingsStatisticsSmallCardSpec?,
    val totalUniqueMusicsPlayed: SettingsStatisticsSmallCardSpec?,
    val totalUniqueArtistsPlayed: SettingsStatisticsSmallCardSpec?,
    val totalListeningTime: SettingsStatisticsSmallCardSpec?,
) {
    val smallCards: List<SettingsStatisticsSmallCardSpec> = listOfNotNull(
        totalListeningTime,
        totalMusicsPlayed,
        totalUniqueMusicsPlayed,
        totalUniqueArtistsPlayed,
    )

    data class MusicStatistics(
        val mostPlayed: ListeningStatistics.MusicStats?,
        val mostListened: ListeningStatistics.MusicStats?,
    ) {

        fun toUiSpec(): List<SettingsStatisticsMostPlayedSongSpec> = buildList {
            mostPlayed?.let {
                add(
                    SettingsStatisticsMostPlayedSongSpec(
                        title = CoreRes.string.most_played_song.toTextSpec(),
                        music = it.music,
                        value = SoulTexts.plays(it.nbPlayed),
                    )
                )
            }
            mostListened?.let {
                add(
                    SettingsStatisticsMostPlayedSongSpec(
                        title = CoreRes.string.most_listened_song.toTextSpec(),
                        music = it.music,
                        value = SoulTexts.compactDuration(it.timeListened),
                    )
                )
            }
        }

        companion object {
            fun new(
                periodGeneralStatistics: PeriodGeneralStatistics
            ): MusicStatistics =
                MusicStatistics(
                    mostPlayed = periodGeneralStatistics.mostPlayedMusic,
                    mostListened = periodGeneralStatistics.mostListenedMusic,
                )
        }
    }

    companion object {
        fun new(
            periodGeneralStatistics: PeriodGeneralStatistics
        ): PeriodGeneralUiStatistics =
            PeriodGeneralUiStatistics(
                musicStatistics = MusicStatistics.new(periodGeneralStatistics),
                totalMusicsPlayed = periodGeneralStatistics.totalMusicsPlayed.takeIf { it > 0 }?.let {
                    SettingsStatisticsSmallCardSpec(
                        title = CoreRes.string.statistics_songs_played_label.toTextSpec(),
                        text = it.toString().toTextSpec(),
                        description = CoreRes.string.statistics_songs_played_subtitle.toTextSpec(),
                    )
                },
                totalUniqueMusicsPlayed = periodGeneralStatistics.totalUniqueMusicsPlayed.takeIf { it > 0 }?.let {
                    SettingsStatisticsSmallCardSpec(
                        title = CoreRes.string.statistics_unique_songs_label.toTextSpec(),
                        text = it.toString().toTextSpec(),
                        description = CoreRes.string.statistics_unique_songs_subtitle.toTextSpec(),
                    )
                },
                totalUniqueArtistsPlayed = periodGeneralStatistics.totalUniqueArtistsPlayed.takeIf { it > 0 }?.let {
                    SettingsStatisticsSmallCardSpec(
                        title = CoreRes.string.statistics_unique_artists_label.toTextSpec(),
                        text = it.toString().toTextSpec(),
                        description = CoreRes.string.statistics_unique_artists_subtitle.toTextSpec(),
                    )
                },
                totalListeningTime = periodGeneralStatistics.totalListeningTime.takeIf { it != Duration.ZERO }?.let {
                    SettingsStatisticsSmallCardSpec(
                        title = CoreRes.string.statistics_listening_time_label.toTextSpec(),
                        text = SoulTexts.compactDuration(it),
                        description = CoreRes.string.statistics_listening_time_subtitle.toTextSpec(),
                    )
                },
            )
    }
}
