package com.github.enteraname74.domain.model.player

import kotlin.uuid.Uuid

data class PlayedListToContinue(
    val playedListId: Uuid,
    val currentMusic: CurrentMusic,
) {
    data class CurrentMusic(
        val name: String,
        val artists: String,
    )
}
