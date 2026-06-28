package com.github.enteraname74.soulsearching.feature.settings.cloud.explanations

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolder

class SettingsCloudExplanationsViewHolder : SoulViewModelHolder<
        SettingsCloudExplanationsAction,
        SettingsCloudExplanationsNavScope,
        SettingsCloudExplanationsState>(
    initialState = SettingsCloudExplanationsState
), SettingsCloudExplanationsAction{
    override val actions: SettingsCloudExplanationsAction = this

    @Composable
    override fun Content(
        actions: SettingsCloudExplanationsAction,
        state: SettingsCloudExplanationsState
    ) {
        SettingsCloudExplanationsScreen()
    }

}