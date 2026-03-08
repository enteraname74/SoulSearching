package com.github.enteraname74.localdb.model.player

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/*
No foreign keys as the timing can sometimes leads to insertion with legacy data just after a change
in the list/current music.
 */
@Entity
data class RoomPlayerMusicProgress(
    @PrimaryKey
    val id: String = ID,
    val playedListId: UUID,
    val playerMusicId: String,
    val progress: Int,
) {
    private companion object {
        const val ID = "RoomPlayerMusicProgressId"
    }
}
