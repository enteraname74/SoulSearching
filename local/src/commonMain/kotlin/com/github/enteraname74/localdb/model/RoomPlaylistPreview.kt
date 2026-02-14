package com.github.enteraname74.localdb.model

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.PlaylistPreview
import java.util.UUID

data class RoomPlaylistPreview(
    val id: UUID,
    val isFavorite: Boolean,
    val name: String,
    val totalMusics : Int,
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
            isInQuickAccess = isInQuickAccess
        )
}
