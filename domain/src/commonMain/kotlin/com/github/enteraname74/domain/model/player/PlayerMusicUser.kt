package com.github.enteraname74.domain.model.player

import java.util.*
import kotlin.uuid.Uuid


data class PlayerMusicUser(
    val playedListId: Uuid,
    val userId: Uuid,
    val musicId: UUID,
)

data class FullPlayerMusicUser(
    val playedListId: Uuid,
    val user: SharedPlayedListUser?,
    val musicId: UUID,
)
