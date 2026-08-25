package com.github.enteraname74.domain.model.statistics

import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.LocalMonthYear
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.util.DateUtils
import kotlin.time.Duration
import kotlin.uuid.Uuid

sealed interface ListeningStatistics {
    val id: Uuid
    val nbPlayed: Int
    val localMonthYear: LocalMonthYear

    fun increment(): ListeningStatistics

    data class PlaylistStats(
        val playlist: PlaylistPreview,
        override val id: Uuid,
        override val nbPlayed: Int,
        override val localMonthYear: LocalMonthYear,
    ) : ListeningStatistics {
        override fun increment(): ListeningStatistics = copy(nbPlayed = nbPlayed + 1)

        companion object {
            fun new(playlist: PlaylistPreview): PlaylistStats = PlaylistStats(
                playlist = playlist,
                id = Uuid.random(),
                nbPlayed = 0,
                localMonthYear = DateUtils.currentMonthYear(),
            )
        }
    }

    data class ArtistStats(
        val artist: ArtistPreview,
        override val id: Uuid,
        override val nbPlayed: Int,
        override val localMonthYear: LocalMonthYear,
    ) : ListeningStatistics {
        override fun increment(): ListeningStatistics = copy(nbPlayed = nbPlayed + 1)

        companion object {
            fun new(artist: ArtistPreview): ArtistStats = ArtistStats(
                artist = artist,
                id = Uuid.random(),
                nbPlayed = 0,
                localMonthYear = DateUtils.currentMonthYear(),
            )
        }
    }

    data class AlbumStats(
        val album: AlbumPreview,
        override val id: Uuid,
        override val nbPlayed: Int,
        override val localMonthYear: LocalMonthYear,
    ) : ListeningStatistics {
        override fun increment(): ListeningStatistics = copy(nbPlayed = nbPlayed + 1)

        companion object {
            fun new(album: AlbumPreview): AlbumStats = AlbumStats(
                album = album,
                id = Uuid.random(),
                nbPlayed = 0,
                localMonthYear = LocalMonthYear(9, 2026),
            )
        }
    }

    data class MusicStats(
        val music: Music,
        val timeListened: Duration,
        override val id: Uuid,
        override val nbPlayed: Int,
        override val localMonthYear: LocalMonthYear,
    ) : ListeningStatistics {
        override fun increment(): ListeningStatistics = copy(nbPlayed = nbPlayed + 1)

        companion object {
            fun new(music: Music): MusicStats = MusicStats(
                music = music,
                id = Uuid.random(),
                nbPlayed = 0,
                timeListened = Duration.ZERO,
                localMonthYear = DateUtils.currentMonthYear(),
            )
        }
    }
}