package com.github.enteraname74.soulsearching.feature.settings.shortcuts

import com.github.enteraname74.soulsearching.feature.settings.shortcuts.ext.commands
import com.github.enteraname74.soulsearching.feature.settings.shortcuts.ext.description
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager

data class SettingsShortcutsState(
    val shortcuts: List<Shortcut> = buildShortcuts()
) {
    data class Shortcut(
        val commands: List<String>,
        val description: String,
    ) {
        companion object {
            fun fromKeyboardAction(keyboardAction: PlaybackManager.KeyboardAction): Shortcut =
                Shortcut(
                    commands = keyboardAction.commands(),
                    description = keyboardAction.description(),
                )
        }
    }

    companion object {
        fun buildShortcuts(): List<Shortcut> =
            PlaybackManager.KeyboardAction.entries.map {
                Shortcut.fromKeyboardAction(it)
            }
    }
}


