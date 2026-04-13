package com.github.enteraname74.localdb.view

import androidx.room.DatabaseView
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Cover.CoverFile.DevicePathSpec
import java.time.LocalDateTime
import java.util.UUID

@DatabaseView(
    """
        SELECT 
        artist.artistId AS id, 
        artist.artistName AS name, 
        artist.coverFolderKey,
        artist.addedDate, 
        artist.nbPlayed, 
        artist.coverUrl, 
        (SELECT COUNT(*) FROM RoomMusicArtist AS musicArtist WHERE musicArtist.artistId = artist.artistId) AS totalMusics, 
        (
            CASE WHEN artist.coverId IS NULL THEN 
                (
                    SELECT music.coverId FROM RoomMusic AS music 
                    INNER JOIN RoomMusicArtist AS musicArtist 
                    ON music.musicId = musicArtist.musicId 
                    AND artist.artistId = musicArtist.artistId 
                    AND music.isHidden = 0 
                    AND scope != 'SharedPlayedList' 
                    AND music.coverId IS NOT NULL 
                    ORDER BY name ASC 
                    LIMIT 1
                )
            ELSE artist.coverId END
        ) AS coverId,
        (
            SELECT music.localPath FROM RoomMusic AS music 
            INNER JOIN RoomMusicArtist AS musicArtist 
            ON music.musicId = musicArtist.musicId 
            AND artist.artistId = musicArtist.artistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
            ORDER BY name ASC 
            LIMIT 1
        ) AS musicCoverPath,
        (
            SELECT music.coverUrl FROM RoomMusic AS music 
            INNER JOIN RoomMusicArtist AS musicArtist 
            ON music.musicId = musicArtist.musicId 
            AND artist.artistId = musicArtist.artistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
            ORDER BY name ASC 
            LIMIT 1
        ) AS musicCoverUrl,
        artist.isInQuickAccess 
        FROM RoomArtist AS artist 
    """
)
data class RoomArtistPreview(
    val id: UUID,
    val name: String,
    val addedDate: LocalDateTime,
    val nbPlayed: Int,
    val totalMusics: Int,
    val coverId: UUID?,
    val coverUrl: String?,
    val musicCoverUrl: String?,
    val coverFolderKey: String?,
    val musicCoverPath: String?,
    val isInQuickAccess: Boolean,
) {
    fun toArtistPreview(): ArtistPreview {
        val localCover = Cover.CoverFile(
            initialCoverPath = musicCoverPath,
            fileCoverId = coverId,
            devicePathSpec = coverFolderKey?.let { key ->
                DevicePathSpec(
                    settingsKey = key,
                    dynamicElementName = name,
                    fallback = Cover.CoverFile(fileCoverId = coverId),
                )
            },
        )
        val remoteCover = coverUrl?.let { Cover.Url(it) } ?: musicCoverUrl?.let { Cover.Url(it) }

        val usedCover = if (remoteCover == null) {
            localCover
        } else {
            localCover.takeIf { !it.isEmpty() } ?: remoteCover
        }

        return ArtistPreview(
            id = id,
            name = name,
            totalMusics = totalMusics,
            cover = usedCover,
            isInQuickAccess = isInQuickAccess
        )
    }
}