package com.github.enteraname74.soulsearching.ext

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager

fun KeyEvent.toKeyboardAction(): PlaybackManager.KeyboardAction? =
    if (isCtrlPressed && type == KeyEventType.KeyUp) {
        when (key) {
            Key.P -> PlaybackManager.KeyboardAction.TogglePlayPause
            Key.B, Key.DirectionLeft -> PlaybackManager.KeyboardAction.Previous
            Key.N, Key.DirectionRight -> PlaybackManager.KeyboardAction.Next
            else -> null
        }
    } else {
        null
    }