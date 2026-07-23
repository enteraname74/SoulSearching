package com.github.enteraname74.soulsearching.feature.settings.shortcuts.ext

import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager

internal fun PlaybackManager.KeyboardAction.description(): String =
    when (this) {
        PlaybackManager.KeyboardAction.TogglePlayPause -> strings.shortcutTogglePlayPauseDescription
        PlaybackManager.KeyboardAction.Previous -> strings.shortcutPreviousDescription
        PlaybackManager.KeyboardAction.Next -> strings.shortcutNextDescription
        PlaybackManager.KeyboardAction.SeekBackward -> strings.shortcutSeekBackwardDescription
        PlaybackManager.KeyboardAction.SeekForward -> strings.shortcutSeekForwardDescription
        PlaybackManager.KeyboardAction.VolumeUp -> strings.shortcutVolumeUpDescription
        PlaybackManager.KeyboardAction.VolumeDown -> strings.shortcutVolumeDownDescription
        PlaybackManager.KeyboardAction.ToggleFavorite -> strings.shortcutToggleFavoriteDescription
    }

internal fun PlaybackManager.KeyboardAction.commands(): List<String> =
    when (this) {
        PlaybackManager.KeyboardAction.TogglePlayPause -> strings.shortcutTogglePlayPauseCommands
        PlaybackManager.KeyboardAction.Previous -> strings.shortcutPreviousCommands
        PlaybackManager.KeyboardAction.Next -> strings.shortcutNextCommands
        PlaybackManager.KeyboardAction.SeekBackward -> strings.shortcutSeekBackwardCommands
        PlaybackManager.KeyboardAction.SeekForward -> strings.shortcutSeekForwardCommands
        PlaybackManager.KeyboardAction.VolumeUp -> strings.shortcutVolumeUpCommands
        PlaybackManager.KeyboardAction.VolumeDown -> strings.shortcutVolumeDownCommands
        PlaybackManager.KeyboardAction.ToggleFavorite -> strings.shortcutToggleFavoriteCommands
    }