package com.github.enteraname74.domain.model.player

/**
 * Possibles playback mode of the player.
 */
enum class PlayerMode(val value: String) {
    Normal(value = "NORMAL"),
    Shuffle(value = "SHUFFLE"),
    Loop(value = "LOOP");

    fun next(): PlayerMode =
        when (this) {
            Normal -> Shuffle
            Shuffle -> Loop
            Loop -> Normal
        }

    companion object {
        fun from(value: String) = entries.firstOrNull { it.value == value } ?: Normal
    }
}