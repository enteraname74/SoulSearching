package com.github.enteraname74.soulsearching.ext

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager

fun KeyEvent.toKeyboardAction(): PlaybackManager.KeyboardAction? =
    if (type == KeyEventType.KeyUp) {
        when (key) {
            Key.Spacebar -> PlaybackManager.KeyboardAction.TogglePlayPause
            Key.DirectionLeft if isShiftPressed -> PlaybackManager.KeyboardAction.Previous
            Key.DirectionRight if isShiftPressed -> PlaybackManager.KeyboardAction.Next
            Key.DirectionLeft -> PlaybackManager.KeyboardAction.SeekBackward
            Key.DirectionRight -> PlaybackManager.KeyboardAction.SeekForward
            Key.DirectionUp -> PlaybackManager.KeyboardAction.VolumeUp
            Key.DirectionDown -> PlaybackManager.KeyboardAction.VolumeDown
            Key.F -> PlaybackManager.KeyboardAction.ToggleFavorite
            else -> null
        }
    } else {
        null
    }