package com.github.enteraname74.localdb.model

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.MusicFolderPreview
import java.util.UUID

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
