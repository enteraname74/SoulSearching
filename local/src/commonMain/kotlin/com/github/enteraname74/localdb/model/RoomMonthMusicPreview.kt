package com.github.enteraname74.localdb.model

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.MonthMusicsPreview
import java.util.UUID

data class RoomMonthMusicPreview(
    val month: String,
    val coverId: UUID?,
    val musicCoverPath: String?,
    val totalMusics: Int,
) {
    fun toMonthMusicsPreview(): MonthMusicsPreview {
        return MonthMusicsPreview(
            month = month,
            cover = Cover.CoverFile(
                fileCoverId = coverId,
                initialCoverPath = musicCoverPath,
            ),
            totalMusics = totalMusics
        )
    }
}
