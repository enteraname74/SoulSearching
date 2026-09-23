package com.github.enteraname74.soulsearching.domain.model.statistics

import com.github.enteraname74.soulsearching.domain.model.AlbumPreview
import com.github.enteraname74.soulsearching.domain.model.ArtistPreview
import com.github.enteraname74.soulsearching.domain.model.LocalMonthYear
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.PlaylistPreview
import com.github.enteraname74.soulsearching.domain.util.DateUtils
import kotlin.time.Duration

sealed interface ListeningStatistics {
    val id: String
    val nbPlayed: Int
    val localMonthYear: LocalMonthYear
    val lastUpdatedMillis: Long

    fun increment(): ListeningStatistics

    data class PlaylistStats(
        val playlist: PlaylistPreview,
        override val id: String,
        override val nbPlayed: Int,
        override val localMonthYear: LocalMonthYear,
        override val lastUpdatedMillis: Long,
    ) : ListeningStatistics {
        override fun increment(): ListeningStatistics = copy(
            nbPlayed = nbPlayed + 1,
            lastUpdatedMillis = DateUtils.now(),
        )

        companion object {
            fun new(playlist: PlaylistPreview): PlaylistStats {
                val localMonthYear = DateUtils.currentMonthYear()

                return PlaylistStats(
                    playlist = playlist,
                    id = "$localMonthYear-${playlist.id}",
                    nbPlayed = 0,
                    localMonthYear = DateUtils.currentMonthYear(),
                    lastUpdatedMillis = DateUtils.now(),
                )
            }
        }
    }

    data class ArtistStats(
        val artist: ArtistPreview,
        override val id: String,
        override val nbPlayed: Int,
        override val localMonthYear: LocalMonthYear,
        override val lastUpdatedMillis: Long,
    ) : ListeningStatistics {
        override fun increment(): ListeningStatistics = copy(
            nbPlayed = nbPlayed + 1,
            lastUpdatedMillis = DateUtils.now(),
        )

        companion object {
            fun new(artist: ArtistPreview): ArtistStats {
                val localMonthYear = DateUtils.currentMonthYear()

                return ArtistStats(
                    artist = artist,
                    id = "$localMonthYear-${artist.id}",
                    nbPlayed = 0,
                    localMonthYear = DateUtils.currentMonthYear(),
                    lastUpdatedMillis = DateUtils.now(),
                )
            }
        }
    }

    data class AlbumStats(
        val album: AlbumPreview,
        override val id: String,
        override val nbPlayed: Int,
        override val localMonthYear: LocalMonthYear,
        override val lastUpdatedMillis: Long,
    ) : ListeningStatistics {
        override fun increment(): ListeningStatistics = copy(
            nbPlayed = nbPlayed + 1,
            lastUpdatedMillis = DateUtils.now(),
        )

        companion object {
            fun new(album: AlbumPreview): AlbumStats {
                val localMonthYear = DateUtils.currentMonthYear()

                return AlbumStats(
                    album = album,
                    id = "$localMonthYear-${album.id}",
                    nbPlayed = 0,
                    localMonthYear = DateUtils.currentMonthYear(),
                    lastUpdatedMillis = DateUtils.now(),
                )
            }
        }
    }

    data class MusicStats(
        val music: Music,
        val timeListened: Duration,
        override val id: String,
        override val nbPlayed: Int,
        override val localMonthYear: LocalMonthYear,
        override val lastUpdatedMillis: Long,
    ) : ListeningStatistics {
        override fun increment(): ListeningStatistics = copy(
            nbPlayed = nbPlayed + 1,
            lastUpdatedMillis = DateUtils.now(),
        )

        companion object {
            fun new(music: Music): MusicStats {
                val localMonthYear = DateUtils.currentMonthYear()

                return MusicStats(
                    music = music,
                    id = "$localMonthYear-${music.musicId}",
                    nbPlayed = 0,
                    timeListened = Duration.ZERO,
                    localMonthYear = localMonthYear,
                    lastUpdatedMillis = DateUtils.now(),
                )
            }
        }
    }
}