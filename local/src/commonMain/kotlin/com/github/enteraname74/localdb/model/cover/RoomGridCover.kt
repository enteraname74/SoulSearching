package com.github.enteraname74.localdb.model.cover

import androidx.room3.Embedded
import com.github.enteraname74.soulsearching.domain.model.Cover

data class RoomGridCover(
    @Embedded("topStart_") val topStart: RoomSimpleCover?,
    @Embedded("topEnd_") val topEnd: RoomSimpleCover?,
    @Embedded("bottomStart_") val bottomStart: RoomSimpleCover?,
    @Embedded("bottomEnd_") val bottomEnd: RoomSimpleCover?,
) {
    fun toGridCover(
        dynamicElementName: String?,
    ): Cover.Grid? {
        val topStart = topStart?.toSimpleCover(dynamicElementName)
        val topEnd = topEnd?.toSimpleCover(dynamicElementName)
        val bottomStart = bottomStart?.toSimpleCover(dynamicElementName)
        val bottomEnd = bottomEnd?.toSimpleCover(dynamicElementName)

        val hasAny = listOfNotNull(
            topStart,
            topEnd,
            bottomStart,
            bottomEnd,
        ).isNotEmpty()

        if (!hasAny) return null

        return Cover.Grid(
            topStart = topStart,
            topEnd = topEnd,
            bottomStart = bottomStart,
            bottomEnd = bottomEnd,
        )
    }
}

internal fun Cover.Grid.toRoomGridCover(): RoomGridCover =
    RoomGridCover(
        topStart = topStart?.toRoomSimpleCover(),
        topEnd = topEnd?.toRoomSimpleCover(),
        bottomStart = bottomStart?.toRoomSimpleCover(),
        bottomEnd = bottomEnd?.toRoomSimpleCover(),
    )
