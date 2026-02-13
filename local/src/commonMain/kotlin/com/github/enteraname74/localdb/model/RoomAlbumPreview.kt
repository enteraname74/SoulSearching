package com.github.enteraname74.localdb.model

import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.Cover
import java.util.UUID

data class RoomAlbumPreview(
    val id: UUID,
    val nbPlayed: Int,
    val name: String,
    val artist: String,
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
