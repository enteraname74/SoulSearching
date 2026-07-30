package com.github.enteraname74.localdb.view

import androidx.room3.DatabaseView
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.PlaylistPreview
import kotlin.time.Instant
import kotlin.uuid.Uuid

@DatabaseView(
    """
        SELECT playlist.playlistId AS id, 
        playlist.name, 
        playlist.isFavorite, 
        playlist.addedDate, 
        playlist.coverUrl, 
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
                    AND scope != 'SharedPlayedList' 
                    AND music.coverId IS NOT NULL 
                    LIMIT 1
                )
            ELSE playlist.coverId END
        ) AS coverId,
        (
            SELECT music.localPath FROM RoomMusic AS music 
            INNER JOIN RoomMusicPlaylist AS musicPlaylist 
            ON music.musicId = musicPlaylist.musicId 
            AND playlist.playlistId = musicPlaylist.playlistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
            LIMIT 1
        ) AS musicCoverPath,
        (
            SELECT music.coverUrl FROM RoomMusic AS music 
            INNER JOIN RoomMusicPlaylist AS musicPlaylist 
            ON music.musicId = musicPlaylist.musicId 
            AND playlist.playlistId = musicPlaylist.playlistId 
            AND music.isHidden = 0 
            AND scope != 'SharedPlayedList' 
            LIMIT 1
        ) AS musicCoverUrl,
        playlist.isInQuickAccess, 
        playlist.nbPlayed 
        FROM RoomPlaylist AS playlist 
    """
)
data class RoomPlaylistPreview(
    val id: Uuid,
    val isFavorite: Boolean,
    val addedDate: Instant,
    val name: String,
    val totalMusics: Int,
    val nbPlayed: Int,
    val coverId: Uuid?,
    val coverUrl: String?,
    val musicCoverUrl: String?,
    val musicCoverPath: String?,
    val isInQuickAccess: Boolean,
) {
    fun toPlaylistPreview(): PlaylistPreview {
        val localCover = Cover.CoverFile(
            initialCoverPath = musicCoverPath,
            fileCoverId = coverId,
        )

        val usedCover: Cover? = when {
            coverId != null -> localCover
            coverUrl != null -> Cover.Url(coverUrl)
            musicCoverPath != null -> localCover
            musicCoverUrl != null -> Cover.Url(musicCoverUrl)
            else -> null
        }

        return PlaylistPreview(
            id = id,
            isFavorite = isFavorite,
            name = name,
            totalMusics = totalMusics,
            cover = usedCover,
            isInQuickAccess = isInQuickAccess,
            nbPlayed = nbPlayed,
        )
    }

}
