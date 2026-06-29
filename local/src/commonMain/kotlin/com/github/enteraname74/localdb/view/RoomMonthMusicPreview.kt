package com.github.enteraname74.localdb.view

import androidx.room.DatabaseView
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.MonthMusicsPreview
import java.util.UUID

@DatabaseView(
    """
        SELECT 
                strftime('%m/%Y', monthMusic.addedDate) AS month,
                COUNT(*) AS totalMusics, 
                (
                    SELECT music.coverId FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND music.coverId IS NOT NULL 
                    AND strftime('%m/%Y', music.addedDate) = strftime('%m/%Y', monthMusic.addedDate)
                    ORDER BY
                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, 
                    name 
                    LIMIT 1
                ) AS coverId,
                (
                    SELECT music.localPath FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND strftime('%m/%Y', music.addedDate) = strftime('%m/%Y', monthMusic.addedDate) 
                    ORDER BY name 
                    LIMIT 1 
                ) AS musicCoverPath, 
                (
                    SELECT music.coverUrl FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND strftime('%m/%Y', music.addedDate) = strftime('%m/%Y', monthMusic.addedDate) 
                    ORDER BY name 
                    LIMIT 1 
                ) AS musicCoverUrl 
            FROM RoomMusic AS monthMusic
            WHERE isHidden = 0 
            GROUP BY strftime('%Y-%m', addedDate) 
            ORDER BY strftime('%Y-%m', addedDate) DESC
    """
)
data class RoomMonthMusicPreview(
    val month: String,
    val coverId: UUID?,
    val musicCoverPath: String?,
    val musicCoverUrl: String?,
    val totalMusics: Int,
) {
    fun toMonthMusicsPreview(): MonthMusicsPreview {
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

        return MonthMusicsPreview(
            month = month,
            cover = usedCover,
            totalMusics = totalMusics
        )
    }

}