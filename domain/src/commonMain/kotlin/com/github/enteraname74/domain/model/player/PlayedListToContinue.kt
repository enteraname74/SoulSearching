package com.github.enteraname74.domain.model.player

import java.util.UUID

data class PlayedListToContinue(
    val playedListId: UUID,
    val currentMusic: CurrentMusic,
) {
    data class CurrentMusic(
        val name: String,
        val artists: String,
    )
}
