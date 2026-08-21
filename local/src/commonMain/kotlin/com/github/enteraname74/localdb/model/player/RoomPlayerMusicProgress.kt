package com.github.enteraname74.localdb.model.player

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlin.uuid.Uuid

/*
No foreign keys as the timing can sometimes lead to insertion with legacy data just after a change
in the list/current music.
 */
@Entity
data class RoomPlayerMusicProgress(
    @PrimaryKey
    val id: String = ID,
    val playedListId: Uuid,
    val playerMusicId: String,
    val progress: Int,
) {
    companion object {
        const val ID = "RoomPlayerMusicProgressId"
    }
}
