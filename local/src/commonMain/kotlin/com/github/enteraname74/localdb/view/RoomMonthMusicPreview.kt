package com.github.enteraname74.localdb.view

import androidx.room.DatabaseView
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.MonthMusicsPreview
import kotlin.uuid.Uuid

@DatabaseView(
    """
        SELECT 
                strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch') AS month,
                COUNT(*) AS totalMusics, 
                (
                    SELECT music.coverId FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND scope != 'SharedPlayedList' 
                    AND music.coverId IS NOT NULL 
                    AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') = strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch')
                    ORDER BY
                    CASE WHEN music.coverId IS NULL THEN 1 ELSE 0 END, 
                    name 
                    LIMIT 1
                ) AS coverId,
                (
                    SELECT music.localPath FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND scope != 'SharedPlayedList' 
                    AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') = strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch') 
                    ORDER BY name 
                    LIMIT 1 
                ) AS musicCoverPath, 
                (
                    SELECT music.coverUrl FROM RoomMusic AS music 
                    WHERE music.isHidden = 0 
                    AND scope != 'SharedPlayedList' 
                    AND strftime('%m/%Y', music.addedDate / 1000, 'unixepoch') = strftime('%m/%Y', monthMusic.addedDate / 1000, 'unixepoch') 
                    ORDER BY name 
                    LIMIT 1 
                ) AS musicCoverUrl 
            FROM RoomMusic AS monthMusic
            WHERE isHidden = 0 
            AND scope != 'SharedPlayedList' 
            GROUP BY strftime('%Y-%m', addedDate / 1000, 'unixepoch') 
            ORDER BY strftime('%Y-%m', addedDate / 1000, 'unixepoch') DESC
    """
)
data class RoomMonthMusicPreview(
    val month: String,
    val coverId: Uuid?,
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
