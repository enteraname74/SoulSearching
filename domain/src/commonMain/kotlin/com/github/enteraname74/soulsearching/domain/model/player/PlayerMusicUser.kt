package com.github.enteraname74.soulsearching.domain.model.player

import kotlin.uuid.Uuid


data class PlayerMusicUser(
    val playedListId: Uuid,
    val userId: Uuid,
    val musicId: Uuid,
)

data class FullPlayerMusicUser(
    val playedListId: Uuid,
    val user: SharedPlayedListUser?,
    val musicId: Uuid,
)
