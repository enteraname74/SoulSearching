package com.github.enteraname74.soulsearching.feature.settings.shortcuts.ext

import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import org.jetbrains.compose.resources.StringArrayResource

internal fun PlaybackManager.KeyboardAction.description(): SoulTextSpec =
    when (this) {
        PlaybackManager.KeyboardAction.TogglePlayPause -> CoreRes.string.shortcut_toggle_play_pause_description.toTextSpec()
        PlaybackManager.KeyboardAction.Previous -> CoreRes.string.shortcut_previous_description.toTextSpec()
        PlaybackManager.KeyboardAction.Next -> CoreRes.string.shortcut_next_description.toTextSpec()
        PlaybackManager.KeyboardAction.SeekBackward -> CoreRes.string.shortcut_seek_backward_description.toTextSpec()
        PlaybackManager.KeyboardAction.SeekForward -> CoreRes.string.shortcut_seek_forward_description.toTextSpec()
        PlaybackManager.KeyboardAction.VolumeUp -> CoreRes.string.shortcut_volume_up_description.toTextSpec()
        PlaybackManager.KeyboardAction.VolumeDown -> CoreRes.string.shortcut_volume_down_description.toTextSpec()
        PlaybackManager.KeyboardAction.ToggleFavorite -> CoreRes.string.shortcut_toggle_favorite_description.toTextSpec()
    }

internal fun PlaybackManager.KeyboardAction.commands(): StringArrayResource =
    when (this) {
        PlaybackManager.KeyboardAction.TogglePlayPause -> CoreRes.array.shortcut_toggle_play_pause_commands
        PlaybackManager.KeyboardAction.Previous -> CoreRes.array.shortcut_previous_commands
        PlaybackManager.KeyboardAction.Next -> CoreRes.array.shortcut_next_commands
        PlaybackManager.KeyboardAction.SeekBackward -> CoreRes.array.shortcut_seek_backward_commands
        PlaybackManager.KeyboardAction.SeekForward -> CoreRes.array.shortcut_seek_forward_commands
        PlaybackManager.KeyboardAction.VolumeUp -> CoreRes.array.shortcut_volume_up_commands
        PlaybackManager.KeyboardAction.VolumeDown -> CoreRes.array.shortcut_volume_down_commands
        PlaybackManager.KeyboardAction.ToggleFavorite -> CoreRes.array.shortcut_toggle_favorite_commands
    }