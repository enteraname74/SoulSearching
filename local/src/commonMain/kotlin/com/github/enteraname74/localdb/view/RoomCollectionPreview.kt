package com.github.enteraname74.localdb.view

import androidx.room3.DatabaseView
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
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
                      OR EXISTS (
                          SELECT 1
                          FROM RoomCollectionPlaylist AS collectionPlaylist
                          INNER JOIN RoomMusicPlaylist AS musicPlaylist
                              ON musicPlaylist.playlistId = collectionPlaylist.playlistId
                          WHERE collectionPlaylist.collectionId = collection.collectionId
                            AND musicPlaylist.musicId = music.musicId
                      )
                  )
            ) AS totalMusics
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
) {
    fun toCollectionPreview(): CollectionPreview = CollectionPreview(
        id = id,
        remoteId = remoteId,
        name = name,
        totalMusics = totalMusics,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
    )

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
