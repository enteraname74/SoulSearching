package com.github.enteraname74.localdb.view

import androidx.room3.DatabaseView
import androidx.room3.Embedded
import com.github.enteraname74.localdb.model.cover.RoomCover
import com.github.enteraname74.localdb.model.cover.RoomGridCover
import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.PlaylistPreview
import com.github.enteraname74.soulsearching.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.soulsearching.domain.util.DateUtils
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal const val ROOM_PLAYLIST_PREVIEW_QUERY_V22 = """
        WITH distinctPlaylistMusicCovers AS (
            SELECT
                musicPlaylist.playlistId,
                MIN(music.musicId) AS representativeMusicId,
                MIN(music.name) AS firstMusicName
            FROM RoomMusicPlaylist AS musicPlaylist
            INNER JOIN RoomMusic AS music
                ON music.musicId = musicPlaylist.musicId
            WHERE music.isHidden = 0
              AND music.scope != 'SharedPlayedList'
              AND (
                  music.cover_initialCoverPath IS NOT NULL
                  OR music.cover_fileCoverId IS NOT NULL
                  OR (music.cover_url IS NOT NULL AND TRIM(music.cover_url) != '')
              )
            GROUP BY
                musicPlaylist.playlistId,
                music.cover_initialCoverPath,
                music.cover_fileCoverId,
                CASE
                    WHEN music.cover_initialCoverPath IS NULL
                         AND music.cover_fileCoverId IS NULL
                    THEN music.cover_url
                    ELSE NULL
                END
        )
        SELECT playlist.playlistId AS id,
        playlist.remoteId,
        playlist.name,
        playlist.isFavorite,
        playlist.addedDate,
        playlist.cover_simple_initialCoverPath,
        playlist.cover_simple_fileCoverId,
        playlist.cover_simple_url,
        playlist.cover_simple_devicePathSpecKey,
        playlist.cover_grid_topStart_initialCoverPath,
        playlist.cover_grid_topStart_fileCoverId,
        playlist.cover_grid_topStart_url,
        playlist.cover_grid_topStart_devicePathSpecKey,
        playlist.cover_grid_topEnd_initialCoverPath,
        playlist.cover_grid_topEnd_fileCoverId,
        playlist.cover_grid_topEnd_url,
        playlist.cover_grid_topEnd_devicePathSpecKey,
        playlist.cover_grid_bottomStart_initialCoverPath,
        playlist.cover_grid_bottomStart_fileCoverId,
        playlist.cover_grid_bottomStart_url,
        playlist.cover_grid_bottomStart_devicePathSpecKey,
        playlist.cover_grid_bottomEnd_initialCoverPath,
        playlist.cover_grid_bottomEnd_fileCoverId,
        playlist.cover_grid_bottomEnd_url,
        playlist.cover_grid_bottomEnd_devicePathSpecKey,

        derivedTopStart.cover_initialCoverPath AS derived_topStart_initialCoverPath,
        derivedTopStart.cover_fileCoverId AS derived_topStart_fileCoverId,
        derivedTopStart.cover_url AS derived_topStart_url,
        derivedTopStart.cover_devicePathSpecKey AS derived_topStart_devicePathSpecKey,

        derivedTopEnd.cover_initialCoverPath AS derived_topEnd_initialCoverPath,
        derivedTopEnd.cover_fileCoverId AS derived_topEnd_fileCoverId,
        derivedTopEnd.cover_url AS derived_topEnd_url,
        derivedTopEnd.cover_devicePathSpecKey AS derived_topEnd_devicePathSpecKey,

        derivedBottomStart.cover_initialCoverPath AS derived_bottomStart_initialCoverPath,
        derivedBottomStart.cover_fileCoverId AS derived_bottomStart_fileCoverId,
        derivedBottomStart.cover_url AS derived_bottomStart_url,
        derivedBottomStart.cover_devicePathSpecKey AS derived_bottomStart_devicePathSpecKey,

        derivedBottomEnd.cover_initialCoverPath AS derived_bottomEnd_initialCoverPath,
        derivedBottomEnd.cover_fileCoverId AS derived_bottomEnd_fileCoverId,
        derivedBottomEnd.cover_url AS derived_bottomEnd_url,
        derivedBottomEnd.cover_devicePathSpecKey AS derived_bottomEnd_devicePathSpecKey,

        (
            SELECT COUNT(*)
            FROM RoomMusicPlaylist AS musicPlaylist
            WHERE musicPlaylist.playlistId = playlist.playlistId
        ) AS totalMusics,
        playlist.isInQuickAccess,
        playlist.nbPlayed
        FROM RoomPlaylist AS playlist
        LEFT JOIN RoomMusic AS derivedTopStart
            ON derivedTopStart.musicId = (
                SELECT candidate.representativeMusicId
                FROM distinctPlaylistMusicCovers AS candidate
                WHERE candidate.playlistId = playlist.playlistId
                ORDER BY candidate.firstMusicName ASC, candidate.representativeMusicId ASC
                LIMIT 1 OFFSET 0
            )
        LEFT JOIN RoomMusic AS derivedTopEnd
            ON derivedTopEnd.musicId = (
                SELECT candidate.representativeMusicId
                FROM distinctPlaylistMusicCovers AS candidate
                WHERE candidate.playlistId = playlist.playlistId
                ORDER BY candidate.firstMusicName ASC, candidate.representativeMusicId ASC
                LIMIT 1 OFFSET 1
            )
        LEFT JOIN RoomMusic AS derivedBottomStart
            ON derivedBottomStart.musicId = (
                SELECT candidate.representativeMusicId
                FROM distinctPlaylistMusicCovers AS candidate
                WHERE candidate.playlistId = playlist.playlistId
                ORDER BY candidate.firstMusicName ASC, candidate.representativeMusicId ASC
                LIMIT 1 OFFSET 2
            )
        LEFT JOIN RoomMusic AS derivedBottomEnd
            ON derivedBottomEnd.musicId = (
                SELECT candidate.representativeMusicId
                FROM distinctPlaylistMusicCovers AS candidate
                WHERE candidate.playlistId = playlist.playlistId
                ORDER BY candidate.firstMusicName ASC, candidate.representativeMusicId ASC
                LIMIT 1 OFFSET 3
            )
"""

@DatabaseView(ROOM_PLAYLIST_PREVIEW_QUERY_V22)
data class RoomPlaylistPreview(
    val id: Uuid,
    val remoteId: Uuid?,
    val isFavorite: Boolean,
    val addedDate: Instant,
    val name: String,
    val totalMusics: Int,
    val nbPlayed: Int,
    @Embedded("cover_") val storedCover: RoomCover?,
    @Embedded("derived_") val derivedCover: RoomGridCover?,
    val isInQuickAccess: Boolean,
) {
    fun toPlaylistPreview(): PlaylistPreview {
        val derivedCovers = derivedCover.toNonEmptySimpleCovers()
        val generatedCover: Cover? = if (derivedCovers.size >= 4) {
            Cover.Grid(derivedCovers)
        } else {
            derivedCovers.firstOrNull()
        }

        val explicitCover = storedCover?.toCover(null)?.takeUnless { it.isEmpty() }
        val usedCover: Cover? = when (explicitCover) {
            is Cover.Grid -> explicitCover
            is Cover.Simple -> explicitCover.copyIfUrl { it.copy(fallback = derivedCovers.firstOrNull()) }
            null -> generatedCover
        }

        return PlaylistPreview(
            id = id,
            isFavorite = isFavorite,
            name = name,
            totalMusics = totalMusics,
            cover = usedCover,
            isInQuickAccess = isInQuickAccess,
            nbPlayed = nbPlayed,
            remoteId = remoteId,
        )
    }

    fun toPlaylistStats(): ListeningStatistics.PlaylistStats {
        val localMonthYear = DateUtils.currentMonthYear()

        return ListeningStatistics.PlaylistStats(
            playlist = toPlaylistPreview(),
            nbPlayed = nbPlayed,
            // Dummy values, not used here
            id = "$localMonthYear-$id",
            localMonthYear = localMonthYear,
            lastUpdatedMillis = DateUtils.now(),
        )
    }
}

internal fun RoomGridCover?.toNonEmptySimpleCovers(): List<Cover.Simple> {
    if (this == null) return emptyList()

    return listOf(
        topStart,
        topEnd,
        bottomStart,
        bottomEnd,
    ).mapNotNull { roomCover ->
        roomCover?.toSimpleCover(null)?.takeUnless { it.isEmpty() }
    }
}
