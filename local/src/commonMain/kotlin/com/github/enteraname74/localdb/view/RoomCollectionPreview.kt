package com.github.enteraname74.localdb.view

import androidx.room3.DatabaseView
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.soulsearching.domain.util.DateUtils
import kotlin.time.Instant
import kotlin.uuid.Uuid

@DatabaseView(
    """
        SELECT
            collection.collectionId AS id,
            collection.remoteId,
            collection.name,
            collection.addedDate,
            collection.coverUrl,
            collection.nbPlayed,
            collection.isInQuickAccess,
            (
                SELECT COUNT(*)
                FROM RoomMusic AS music
                WHERE music.isHidden = 0
                  AND music.scope != 'SharedPlayedList'
                  AND (
                      EXISTS (
                          SELECT 1
                          FROM RoomCollectionAlbum AS collectionAlbum
                          WHERE collectionAlbum.collectionId = collection.collectionId
                            AND collectionAlbum.albumId = music.albumId
                      )
                      OR EXISTS (
                          SELECT 1
                          FROM RoomCollectionArtist AS collectionArtist
                          INNER JOIN RoomMusicArtist AS musicArtist
                              ON musicArtist.artistId = collectionArtist.artistId
                          WHERE collectionArtist.collectionId = collection.collectionId
                            AND musicArtist.musicId = music.musicId
                      )
                  )
            ) AS totalMusics,
            COALESCE(
                collection.coverId,
                (
                    SELECT music.coverId
                    FROM RoomMusic AS music
                    WHERE music.isHidden = 0
                      AND music.scope != 'SharedPlayedList'
                      AND music.coverId IS NOT NULL
                      AND (
                          EXISTS (SELECT 1 FROM RoomCollectionAlbum ca WHERE ca.collectionId = collection.collectionId AND ca.albumId = music.albumId)
                          OR EXISTS (SELECT 1 FROM RoomCollectionArtist ca INNER JOIN RoomMusicArtist ma ON ma.artistId = ca.artistId WHERE ca.collectionId = collection.collectionId AND ma.musicId = music.musicId)
                      )
                    ORDER BY music.name ASC
                    LIMIT 1
                )
            ) AS coverId,
            (
                SELECT music.localPath
                FROM RoomMusic AS music
                WHERE music.isHidden = 0
                  AND music.scope != 'SharedPlayedList'
                  AND (
                      EXISTS (SELECT 1 FROM RoomCollectionAlbum ca WHERE ca.collectionId = collection.collectionId AND ca.albumId = music.albumId)
                      OR EXISTS (SELECT 1 FROM RoomCollectionArtist ca INNER JOIN RoomMusicArtist ma ON ma.artistId = ca.artistId WHERE ca.collectionId = collection.collectionId AND ma.musicId = music.musicId)
                  )
                ORDER BY music.name ASC
                LIMIT 1
            ) AS musicCoverPath,
            (
                SELECT music.coverUrl
                FROM RoomMusic AS music
                WHERE music.isHidden = 0
                  AND music.scope != 'SharedPlayedList'
                  AND (
                      EXISTS (SELECT 1 FROM RoomCollectionAlbum ca WHERE ca.collectionId = collection.collectionId AND ca.albumId = music.albumId)
                      OR EXISTS (SELECT 1 FROM RoomCollectionArtist ca INNER JOIN RoomMusicArtist ma ON ma.artistId = ca.artistId WHERE ca.collectionId = collection.collectionId AND ma.musicId = music.musicId)
                  )
                ORDER BY music.name ASC
                LIMIT 1
            ) AS musicCoverUrl
        FROM RoomCollection AS collection
    """
)
data class RoomCollectionPreview(
    val id: Uuid,
    val remoteId: Uuid?,
    val name: String,
    val addedDate: Instant,
    val nbPlayed: Int,
    val totalMusics: Int,
    val isInQuickAccess: Boolean,
    val coverId: Uuid?,
    val coverUrl: String?,
    val musicCoverUrl: String?,
    val musicCoverPath: String?,
) {
    fun toCollectionPreview(): CollectionPreview {
        val localCover = Cover.CoverFile(initialCoverPath = musicCoverPath, fileCoverId = coverId)
        val cover = when {
            coverId != null -> localCover
            coverUrl != null -> Cover.Url(
                url = coverUrl,
                fallback = if (localCover.isEmpty() && musicCoverUrl != null) {
                    Cover.Url(musicCoverUrl, localCover)
                } else localCover,
            )
            musicCoverPath != null -> localCover
            musicCoverUrl != null -> Cover.Url(musicCoverUrl, localCover)
            else -> null
        }

        return CollectionPreview(
            id = id,
            remoteId = remoteId,
            name = name,
            totalMusics = totalMusics,
            nbPlayed = nbPlayed,
            cover = cover,
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
