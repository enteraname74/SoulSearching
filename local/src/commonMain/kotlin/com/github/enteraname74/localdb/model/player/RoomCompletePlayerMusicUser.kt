package com.github.enteraname74.localdb.model.player

import androidx.room3.Embedded
import androidx.room3.Relation
import com.github.enteraname74.domain.model.player.FullPlayerMusicUser

data class RoomCompletePlayerMusicUser(
    @Embedded val playerMusicUser: RoomPlayerMusicUser,
    @Relation(
        parentColumns = ["userId"],
        entityColumns = ["userId"],
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
