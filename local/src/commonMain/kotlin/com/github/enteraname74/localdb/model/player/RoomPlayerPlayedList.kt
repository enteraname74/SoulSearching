package com.github.enteraname74.localdb.model.player

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.PlayedListType
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerPlayedList
import kotlin.uuid.Uuid

@Entity
data class RoomPlayerPlayedList(
    @PrimaryKey
    val id: Uuid,
    val playlistId: String?,
    val isMainPlaylist: Boolean,
    val mode: PlayerMode,
    val state: PlayedListState,
    val invitationCode: String?,
    val scope: PlayedListScope,
) {
    fun toPlayerPlayedList(): PlayerPlayedList =
        PlayerPlayedList(
            id = id,
            playlistId = playlistId,
            isMainPlaylist = isMainPlaylist,
            mode = mode,
            state = state,
            scope = scope,
            type = invitationCode?.let {
                PlayedListType.Shared(it)
            } ?: PlayedListType.Local
        )
}

internal fun PlayerPlayedList.toRoomPlayerPlayedList(): RoomPlayerPlayedList =
    RoomPlayerPlayedList(
        id = id,
        playlistId = playlistId,
        isMainPlaylist = isMainPlaylist,
        mode = mode,
        state = state,
        scope = scope,
        invitationCode = (type as? PlayedListType.Shared)?.invitationCode,
    )
