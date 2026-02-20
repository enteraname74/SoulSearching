package com.github.enteraname74.localdb.model.player

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerPlayedList
import java.util.UUID

@Entity
data class RoomPlayerPlayedList(
    @PrimaryKey
    val id: UUID,
    val playlistId: UUID?,
    val isMainPlaylist: Boolean,
    val mode: PlayerMode,
    val state: PlayedListState,
) {
    fun toPlayerPlayedList(): PlayerPlayedList =
        PlayerPlayedList(
            id = id,
            playlistId = playlistId,
            isMainPlaylist = isMainPlaylist,
            mode = mode,
            state = state,
        )
}

internal fun PlayerPlayedList.toRoomPlayerPlayedList(): RoomPlayerPlayedList =
    RoomPlayerPlayedList(
        id = id,
        playlistId = playlistId,
        isMainPlaylist = isMainPlaylist,
        mode = mode,
        state = state,
    )
