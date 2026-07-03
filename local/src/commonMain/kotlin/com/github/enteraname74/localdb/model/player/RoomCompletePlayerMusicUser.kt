package com.github.enteraname74.localdb.model.player

import androidx.room.Embedded
import androidx.room.Relation
import com.github.enteraname74.domain.model.player.FullPlayerMusicUser

data class RoomCompletePlayerMusicUser(
    @Embedded val playerMusicUser: RoomPlayerMusicUser,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId",
        entity = RoomSharedPlayedListUser::class,
    )
    val user: RoomSharedPlayedListUser?
) {
    fun toFullPlayerMusicUser(): FullPlayerMusicUser =
        FullPlayerMusicUser(
            playedListId = playerMusicUser.playedListId,
            user = user?.toSharedPlayedListUser(),
            musicId = playerMusicUser.musicId,
        )
}
