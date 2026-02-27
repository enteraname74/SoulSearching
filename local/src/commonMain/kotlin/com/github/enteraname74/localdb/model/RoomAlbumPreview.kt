package com.github.enteraname74.localdb.model

import androidx.room.DatabaseView
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.Cover
import java.time.LocalDateTime
import java.util.UUID

@DatabaseView(
    """
        SELECT 
        album.albumId AS id, 
        album.albumName AS name, 
        album.nbPlayed, 
        album.addedDate, 
        album.artistId, 
        (SELECT artistName FROM RoomArtist WHERE artistId = album.artistId) AS artist, 
        (
            CASE WHEN album.coverId IS NULL THEN 
                (
                    SELECT music.coverId FROM RoomMusic AS music 
                    WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
                    CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
                    music.albumPosition, 
                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, 
                    music.name 
                )
            ELSE album.coverId END
        ) AS coverId,
        (
            SELECT music.path FROM RoomMusic AS music 
            WHERE music.albumId = album.albumId AND music.isHidden = 0 ORDER BY 
            CASE WHEN music.albumPosition IS NULL THEN 1 ELSE 0 END, 
            music.albumPosition, 
            music.name 
            LIMIT 1 
        ) AS musicCoverPath,
        album.isInQuickAccess 
        FROM RoomAlbum AS album 
    """
)
data class RoomAlbumPreview(
    val id: UUID,
    val nbPlayed: Int,
    val addedDate: LocalDateTime,
    val name: String,
    val artist: String,
    val artistId: UUID,
    val coverId: UUID?,
    val musicCoverPath: String?,
    val isInQuickAccess: Boolean,
) {
    fun toAlbumPreview(): AlbumPreview =
        AlbumPreview(
            id = id,
            name = name,
            artist = artist,
            cover = Cover.CoverFile(
                initialCoverPath = musicCoverPath,
                fileCoverId = coverId,
            ),
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
        )
}
