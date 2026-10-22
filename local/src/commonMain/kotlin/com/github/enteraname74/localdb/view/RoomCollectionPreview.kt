package com.github.enteraname74.localdb.view

import androidx.room3.DatabaseView
import androidx.room3.Embedded
import com.github.enteraname74.localdb.model.cover.RoomCover
import com.github.enteraname74.localdb.model.cover.RoomGridCover
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.soulsearching.domain.util.DateUtils
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal const val ROOM_COLLECTION_PREVIEW_QUERY_V22 = """
        WITH eligibleCollectionMusics AS (
            SELECT collectionAlbum.collectionId, music.musicId
            FROM RoomCollectionAlbum AS collectionAlbum
            INNER JOIN RoomMusic AS music
                ON music.albumId = collectionAlbum.albumId
            WHERE music.isHidden = 0
              AND music.scope != 'SharedPlayedList'

            UNION

            SELECT collectionArtist.collectionId, music.musicId
            FROM RoomCollectionArtist AS collectionArtist
            INNER JOIN RoomMusicArtist AS musicArtist
                ON musicArtist.artistId = collectionArtist.artistId
            INNER JOIN RoomMusic AS music
                ON music.musicId = musicArtist.musicId
            WHERE music.isHidden = 0
              AND music.scope != 'SharedPlayedList'
        ),
        distinctCollectionMusicCovers AS (
            SELECT
                collectionMusic.collectionId,
                MIN(music.musicId) AS representativeMusicId,
                MIN(music.name) AS firstMusicName
            FROM eligibleCollectionMusics AS collectionMusic
            INNER JOIN RoomMusic AS music
                ON music.musicId = collectionMusic.musicId
            WHERE music.cover_initialCoverPath IS NOT NULL
               OR music.cover_fileCoverId IS NOT NULL
               OR (music.cover_url IS NOT NULL AND TRIM(music.cover_url) != '')
            GROUP BY
                collectionMusic.collectionId,
                music.cover_initialCoverPath,
                music.cover_fileCoverId,
                CASE
                    WHEN music.cover_initialCoverPath IS NULL
                         AND music.cover_fileCoverId IS NULL
                    THEN music.cover_url
                    ELSE NULL
                END
        )
        SELECT
            collection.collectionId AS id,
            collection.remoteId,
            collection.name,
            collection.addedDate,
            collection.cover_simple_initialCoverPath,
            collection.cover_simple_fileCoverId,
            collection.cover_simple_url,
            collection.cover_simple_devicePathSpecKey,
            collection.cover_grid_topStart_initialCoverPath,
            collection.cover_grid_topStart_fileCoverId,
            collection.cover_grid_topStart_url,
            collection.cover_grid_topStart_devicePathSpecKey,
            collection.cover_grid_topEnd_initialCoverPath,
            collection.cover_grid_topEnd_fileCoverId,
            collection.cover_grid_topEnd_url,
            collection.cover_grid_topEnd_devicePathSpecKey,
            collection.cover_grid_bottomStart_initialCoverPath,
            collection.cover_grid_bottomStart_fileCoverId,
            collection.cover_grid_bottomStart_url,
            collection.cover_grid_bottomStart_devicePathSpecKey,
            collection.cover_grid_bottomEnd_initialCoverPath,
            collection.cover_grid_bottomEnd_fileCoverId,
            collection.cover_grid_bottomEnd_url,
            collection.cover_grid_bottomEnd_devicePathSpecKey,

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

            collection.nbPlayed,
            collection.isInQuickAccess,
            (
                SELECT COUNT(*)
                FROM eligibleCollectionMusics AS collectionMusic
                WHERE collectionMusic.collectionId = collection.collectionId
            ) AS totalMusics
        FROM RoomCollection AS collection
        LEFT JOIN RoomMusic AS derivedTopStart
            ON derivedTopStart.musicId = (
                SELECT candidate.representativeMusicId
                FROM distinctCollectionMusicCovers AS candidate
                WHERE candidate.collectionId = collection.collectionId
                ORDER BY candidate.firstMusicName ASC, candidate.representativeMusicId ASC
                LIMIT 1 OFFSET 0
            )
        LEFT JOIN RoomMusic AS derivedTopEnd
            ON derivedTopEnd.musicId = (
                SELECT candidate.representativeMusicId
                FROM distinctCollectionMusicCovers AS candidate
                WHERE candidate.collectionId = collection.collectionId
                ORDER BY candidate.firstMusicName ASC, candidate.representativeMusicId ASC
                LIMIT 1 OFFSET 1
            )
        LEFT JOIN RoomMusic AS derivedBottomStart
            ON derivedBottomStart.musicId = (
                SELECT candidate.representativeMusicId
                FROM distinctCollectionMusicCovers AS candidate
                WHERE candidate.collectionId = collection.collectionId
                ORDER BY candidate.firstMusicName ASC, candidate.representativeMusicId ASC
                LIMIT 1 OFFSET 2
            )
        LEFT JOIN RoomMusic AS derivedBottomEnd
            ON derivedBottomEnd.musicId = (
                SELECT candidate.representativeMusicId
                FROM distinctCollectionMusicCovers AS candidate
                WHERE candidate.collectionId = collection.collectionId
                ORDER BY candidate.firstMusicName ASC, candidate.representativeMusicId ASC
                LIMIT 1 OFFSET 3
            )
"""

@DatabaseView(ROOM_COLLECTION_PREVIEW_QUERY_V22)
data class RoomCollectionPreview(
    val id: Uuid,
    val remoteId: Uuid?,
    val name: String,
    val addedDate: Instant,
    val nbPlayed: Int,
    val totalMusics: Int,
    val isInQuickAccess: Boolean,
    @Embedded("cover_") val storedCover: RoomCover?,
    @Embedded("derived_") val derivedCover: RoomGridCover?,
) {
    fun toCollectionPreview(): CollectionPreview {
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

        return CollectionPreview(
            id = id,
            remoteId = remoteId,
            name = name,
            totalMusics = totalMusics,
            nbPlayed = nbPlayed,
            cover = usedCover,
            isInQuickAccess = isInQuickAccess,
        )
    }

    fun toCollectionStats(): ListeningStatistics.CollectionStats {
        val localMonthYear = DateUtils.currentMonthYear()

        return ListeningStatistics.CollectionStats(
            collection = toCollectionPreview(),
            nbPlayed = nbPlayed,
            id = "$localMonthYear-$id",
            localMonthYear = localMonthYear,
            lastUpdatedMillis = DateUtils.now(),
        )
    }
}
