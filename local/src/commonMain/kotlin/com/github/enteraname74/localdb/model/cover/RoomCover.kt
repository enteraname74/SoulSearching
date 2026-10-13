package com.github.enteraname74.localdb.model.cover

import androidx.room3.Embedded
import com.github.enteraname74.soulsearching.domain.model.Cover

data class RoomCover(
    @Embedded("simple_") val simple: RoomSimpleCover?,
    @Embedded("grid_") val grid: RoomGridCover?,
) {
    fun toCover(
        dynamicElementName: String?,
    ): Cover? =
        grid?.toGridCover(dynamicElementName) ?: simple?.toSimpleCover(dynamicElementName)
}

internal fun Cover.toRoomCover(): RoomCover =
    RoomCover(
        simple = (this as? Cover.Simple)?.toRoomSimpleCover(),
        grid = (this as? Cover.Grid)?.takeIf { !it.isEmpty() }?.toRoomGridCover(),
    )
