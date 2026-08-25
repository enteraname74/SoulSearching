package com.github.enteraname74.localdb.view

import androidx.room3.DatabaseView
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.domain.util.DateUtils
import kotlin.time.Instant
import kotlin.uuid.Uuid

@DatabaseView(
    """
        SELECT 
        album.albumId AS id, 
        album.albumName AS name, 
        album.nbPlayed, 
        album.addedDate, 
        album.artistId,
        album.coverUrl, 
        (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist, 
        (
            CASE WHEN album.coverId IS NULL THEN 
                (
                    SELECT music.coverId FROM RoomMusic AS music 
                    WHERE music.albumId = album.albumId AND music.isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY 
                    CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                    music.albumPosition, 
                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, 
                    music.name 
                )
            ELSE album.coverId END
        ) AS coverId,
        (
            SELECT music.localPath FROM RoomMusic AS music 
            WHERE music.albumId = album.albumId AND music.isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY 
            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
            music.albumPosition, 
            music.name 
            LIMIT 1 
        ) AS musicCoverPath,
        (
            SELECT music.coverUrl FROM RoomMusic AS music 
            WHERE music.albumId = album.albumId AND music.isHidden = 0 AND scope != 'SharedPlayedList' ORDER BY 
            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
            music.albumPosition, 
            music.name 
            LIMIT 1 
        ) AS musicCoverUrl,
        album.isInQuickAccess 
        FROM RoomAlbum AS album 
        WHERE album.scope != 'SharedPlayedList'
    """
)
data class RoomAlbumPreview(
    val id: Uuid,
    val nbPlayed: Int,
    val addedDate: Instant,
    val name: String,
    val artist: String,
    val artistId: Uuid,
    val coverId: Uuid?,
    val coverUrl: String?,
    val musicCoverUrl: String?,
    val musicCoverPath: String?,
    val isInQuickAccess: Boolean,
) {
    fun toAlbumPreview(): AlbumPreview {
        val localCover = Cover.CoverFile(
            initialCoverPath = musicCoverPath,
            fileCoverId = coverId,
        )

        val usedCover: Cover? = when {
            coverId != null -> localCover
            coverUrl != null -> {
                val fallback = if (localCover.isEmpty() && musicCoverUrl != null) {
                    Cover.Url(musicCoverUrl, localCover)
                } else {
                    localCover
                }

                Cover.Url(coverUrl, fallback)
            }
            musicCoverPath != null -> localCover
            musicCoverUrl != null -> Cover.Url(musicCoverUrl, localCover)
            else -> null
        }

        return AlbumPreview(
            id = id,
            name = name,
            artist = artist,
            cover = usedCover,
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
        )
    }

    fun toAlbumStats(): ListeningStatistics.AlbumStats =
        ListeningStatistics.AlbumStats(
            album = toAlbumPreview(),
            nbPlayed = nbPlayed,
            // Dummy values, not used here
            id = Uuid.random(),
            localMonthYear = DateUtils.currentMonthYear(),
        )
}
