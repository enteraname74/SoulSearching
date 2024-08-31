package com.github.enteraname74.domain.model

/**
 * Possibles playback mode of the player.
 */
enum class PlayerMode(val value: String) {
    Normal(value = "NORMAL"),
    Shuffle(value = "SHUFFLE"),
    Loop(value = "LOOP");

    companion object {
        fun from(value: String) = entries.firstOrNull { it.value == value } ?: Normal
    }
}