package com.github.enteraname74.localdb.model

import androidx.room.DatabaseView
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.MusicFolderPreview
import java.util.UUID

@DatabaseView(
    """
        SELECT 
                folderMusic.folder,
                COUNT(*) AS totalMusics, 
                (
                    SELECT music.coverId FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND music.coverId IS NOT NULL 
                    AND music.folder = folderMusic.folder 
                    ORDER BY
                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, 
                    name 
                    LIMIT 1
                ) AS coverId,
                (
                    SELECT music.path FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND music.folder = folderMusic.folder 
                    ORDER BY name 
                    LIMIT 1 
                ) AS musicCoverPath 
            FROM RoomMusic As folderMusic
            WHERE isHidden = 0 
            GROUP BY folderMusic.folder 
    """
)
data class RoomMusicFolderPreview(
    val folder: String,
    val coverId: UUID?,
    val musicCoverPath: String?,
    val totalMusics: Int,
) {
    fun toMusicFolderPreview(): MusicFolderPreview =
        MusicFolderPreview(
            folder = folder,
            cover = Cover.CoverFile(
                initialCoverPath = musicCoverPath,
                fileCoverId = coverId,
            ),
            totalMusics = totalMusics,
        )
}
