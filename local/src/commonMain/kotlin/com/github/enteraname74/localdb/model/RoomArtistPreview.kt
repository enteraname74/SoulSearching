package com.github.enteraname74.localdb.model

import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Cover.CoverFile.DevicePathSpec
import java.util.UUID

data class RoomArtistPreview(
    val id: UUID,
    val name: String,
    val totalMusics: Int,
    val coverId: UUID?,
    val coverFolderKey: String?,
    val musicCoverPath: String?,
    val isInQuickAccess: Boolean,
) {
    fun toArtistPreview(): ArtistPreview =
        ArtistPreview(
            id = id,
            name = name,
            totalMusics = totalMusics,
            cover = Cover.CoverFile(
                initialCoverPath = musicCoverPath,
                fileCoverId = coverId,
                devicePathSpec = coverFolderKey?.let { key ->
                    DevicePathSpec(
                        settingsKey = key,
                        dynamicElementName = name,
                        fallback = Cover.CoverFile(fileCoverId = coverId),
                    )
                },
            ),
            isInQuickAccess = isInQuickAccess
        )
}
