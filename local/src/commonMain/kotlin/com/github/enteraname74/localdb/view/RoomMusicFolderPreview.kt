package com.github.enteraname74.localdb.view

import androidx.room.DatabaseView
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.MusicFolderPreview
import kotlin.uuid.Uuid

@DatabaseView(
    """
        SELECT 
                folderMusic.folder,
                COUNT(*) AS totalMusics, 
                (
                    SELECT music.coverId FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND scope != 'SharedPlayedList' 
                    AND music.coverId IS NOT NULL 
                    AND music.folder = folderMusic.folder 
                    ORDER BY
                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, 
                    name 
                    LIMIT 1
                ) AS coverId,
                (
                    SELECT music.localPath FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND scope != 'SharedPlayedList' 
                    AND music.folder = folderMusic.folder 
                    ORDER BY name 
                    LIMIT 1 
                ) AS musicCoverPath, 
                (
                    SELECT music.coverUrl FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND scope != 'SharedPlayedList' 
                    AND music.folder = folderMusic.folder 
                    ORDER BY name 
                    LIMIT 1 
                ) AS musicCoverUrl 
            FROM RoomMusic As folderMusic
            WHERE isHidden = 0 
            AND scope != 'SharedPlayedList' 
            GROUP BY folderMusic.folder 
    """
)
data class RoomMusicFolderPreview(
    val folder: String,
    val coverId: Uuid?,
    val musicCoverPath: String?,
    val musicCoverUrl: String?,
    val totalMusics: Int,
) {
    fun toMusicFolderPreview(): MusicFolderPreview {
        val localCover = Cover.CoverFile(
            initialCoverPath = musicCoverPath,
            fileCoverId = coverId,
        )
        val remoteCover = musicCoverUrl?.let { Cover.Url(it) }

        val usedCover = if (remoteCover == null) {
            localCover
        } else {
            localCover.takeIf { !it.isEmpty() } ?: remoteCover
        }

        return MusicFolderPreview(
            folder = folder,
            cover = usedCover,
            totalMusics = totalMusics,
        )
    }

}
