package com.github.enteraname74.soulsearching.feature.settings.cloud

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_settings_filled
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
                title = strings.cloudSettingsTitle,
                subTitle = strings.cloudSettingsText,
                onClick = actions::toSettings,
                leadIcon = CoreRes.drawable.ic_settings_filled,
                trailIcon = CoreRes.drawable.ic_warning_filled.takeIf { !state.hasUrl },
            )
        }
    }
}