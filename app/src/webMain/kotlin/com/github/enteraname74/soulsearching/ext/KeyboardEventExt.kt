package com.github.enteraname74.soulsearching.ext

import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import org.w3c.dom.events.KeyboardEvent

fun KeyboardEvent.toKeyboardAction(): PlaybackManager.KeyboardAction? =
    when {
        code == "Space" ->
            PlaybackManager.KeyboardAction.TogglePlayPause

        shiftKey && code == "ArrowLeft" ->
            PlaybackManager.KeyboardAction.Previous

        shiftKey && code == "ArrowRight" ->
            PlaybackManager.KeyboardAction.Next

        else -> null
    }