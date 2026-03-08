package com.github.enteraname74.localdb.view

import androidx.room.DatabaseView
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.PlaylistPreview
import java.time.LocalDateTime
import java.util.UUID

@DatabaseView(
    """
        SELECT playlist.playlistId AS id, 
        playlist.name, 
        playlist.isFavorite, 
        playlist.addedDate, 
        (
            SELECT COUNT(*) 
            FROM RoomMusicPlaylist AS musicPlaylist 
            WHERE musicPlaylist.playlistId = playlist.playlistId
        ) AS totalMusics, 
        (
            CASE WHEN playlist.coverId IS NULL THEN 
                (
                    SELECT music.coverId FROM RoomMusic AS music 
                    INNER JOIN RoomMusicPlaylist AS musicPlaylist 
                    ON music.musicId = musicPlaylist.musicId 
                    AND playlist.playlistId = musicPlaylist.playlistId 
                    AND music.isHidden = 0 
                    AND music.coverId IS NOT NULL 
                    LIMIT 1
                )
            ELSE playlist.coverId END
        ) AS coverId,
        (
            SELECT music.path FROM RoomMusic AS music 
            INNER JOIN RoomMusicPlaylist AS musicPlaylist 
            ON music.musicId = musicPlaylist.musicId 
            AND playlist.playlistId = musicPlaylist.playlistId 
            AND music.isHidden = 0 
            LIMIT 1
        ) AS musicCoverPath,
        playlist.isInQuickAccess, 
        playlist.nbPlayed 
        FROM RoomPlaylist AS playlist 
    """
)
data class RoomPlaylistPreview(
    val id: UUID,
    val isFavorite: Boolean,
    val addedDate: LocalDateTime,
    val name: String,
    val totalMusics : Int,
    val nbPlayed: Int,
    val coverId: UUID?,
    val musicCoverPath: String?,
    val isInQuickAccess: Boolean,
) {
    fun toPlaylistPreview(): PlaylistPreview =
        PlaylistPreview(
            id = id,
            isFavorite = isFavorite,
            name = name,
            totalMusics = totalMusics,
            cover = Cover.CoverFile(
                initialCoverPath = musicCoverPath,
                fileCoverId = coverId,
            ),
            isInQuickAccess = isInQuickAccess,
            nbPlayed = nbPlayed,
        )
}