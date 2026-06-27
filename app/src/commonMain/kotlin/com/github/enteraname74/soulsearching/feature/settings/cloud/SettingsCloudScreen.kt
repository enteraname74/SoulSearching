package com.github.enteraname74.soulsearching.feature.settings.cloud

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_chevron_right
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_groups_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_music_note_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_person_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_play_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_settings_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_sync_alt
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_warning_filled
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsCloudScreen(
    actions: SettingsCloudActions,
    state: SettingsCloudState,
) {
    SettingPage(
        navigateBack = actions::navigateBack,
        title = strings.cloudTitle,
    ) {
        item {
            SoulMenuElement(
                title = if (state.user == null) {
                    strings.cloudSignIn
                } else {
                    state.user.username
                },
                subTitle = state.user?.let { strings.userType(it.type) },
                onClick = actions::onUserClick,
                leadIcon = CoreRes.drawable.ic_person_filled,
                trailIcon = CoreRes.drawable.ic_chevron_right,
            )
        }

        item {
            SoulMenuElement(
                title = strings.cloudSettingsTitle,
                subTitle = strings.cloudSettingsText,
                onClick = actions::toSettings,
                leadIcon = CoreRes.drawable.ic_settings_filled,
                trailIcon = CoreRes.drawable.ic_warning_filled.takeIf { state.hasUrl == false },
            )
        }

        item {
            SoulMenuElement(
                enabled = state.connectedFeaturesEnabled,
                title = strings.cloudUsersTitle,
                subTitle = strings.cloudUsersText,
                onClick = actions::toUsers,
                leadIcon = CoreRes.drawable.ic_groups_filled,
            )
        }

        item {
            SoulMenuElement(
                enabled = state.connectedFeaturesEnabled,
                title = strings.cloudSyncTitle,
                subTitle = strings.cloudSyncText,
                onClick = actions::toSync,
                leadIcon = CoreRes.drawable.ic_sync_alt,
            )
        }

        item {
            SoulMenuElement(
                enabled = state.connectedFeaturesEnabled,
                title = strings.cloudFetchMusicTitle,
                subTitle = strings.cloudFetchMusicText,
                onClick = actions::toFetchMusic,
                leadIcon = CoreRes.drawable.ic_music_note_filled,
            )
        }

        item {
            SoulMenuElement(
                enabled = state.connectedFeaturesEnabled,
                title = strings.cloudSharedListTitle,
                subTitle = strings.cloudSharedListText,
                onClick = actions::toSharedList,
                leadIcon = CoreRes.drawable.ic_play_filled,
            )
        }
    }
}