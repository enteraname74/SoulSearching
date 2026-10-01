package com.github.enteraname74.soulsearching.feature.settings.shortcuts

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder

class SettingsShortcutsViewHolder : SoulViewModelHolder<
    SettingsShortcutsActions,
    SettingsShortcutsNavScope,
    SettingsShortcutsState>(
    initialState = SettingsShortcutsState()
), SettingsShortcutsActions {
    override val actions: SettingsShortcutsActions = this

    override fun navigateBack() {
        navigate { navigateBack() }
    }

    @Composable
    override fun Content(
        actions: SettingsShortcutsActions,
        state: SettingsShortcutsState,
    ) {
        SettingsShortcutsScreen(
            state = state,
            actions = actions,
        )
    }
}