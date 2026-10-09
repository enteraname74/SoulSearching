package com.github.enteraname74.localdb.model.cover

import com.github.enteraname74.soulsearching.domain.model.Cover
import kotlin.uuid.Uuid

data class RoomSimpleCover(
    val initialCoverPath: String?,
    val fileCoverId: Uuid?,
    val url: String?,
    val devicePathSpecKey: String?,
) {
    fun toSimpleCover(
        dynamicElementName: String?,
    ): Cover.Simple {
        val localCover = Cover.CoverFile(
            fileCoverId = fileCoverId,
            initialCoverPath = initialCoverPath,
            devicePathSpec = if (devicePathSpecKey != null && dynamicElementName != null) {
                Cover.CoverFile.DevicePathSpec(
                    settingsKey = devicePathSpecKey,
                    dynamicElementName = dynamicElementName,
                    fallback = Cover.CoverFile(fileCoverId = fileCoverId),
                )
            } else {
                null
            }
        )

        val remoteCover = url?.let { Cover.Url(it, localCover) }

        return if (remoteCover == null) {
            localCover
        } else {
            localCover.takeIf { !it.isEmpty() } ?: remoteCover
        }
    }
}

internal fun Cover.Simple.toRoomSimpleCover(): RoomSimpleCover =
    RoomSimpleCover(
        initialCoverPath = (this as? Cover.CoverFile)?.initialCoverPath,
        fileCoverId = (this as? Cover.CoverFile)?.fileCoverId,
        url = (this as? Cover.Url)?.url,
        devicePathSpecKey = (this as? Cover.CoverFile)?.devicePathSpec?.settingsKey,
    )