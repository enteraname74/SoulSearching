package com.github.enteraname74.soulsearching.feature.settings.cloud.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsCloudSettingsScreen(
    actions: SettingsCloudSettingsActions,
    state: SettingsCloudSettingsState,
) {
    SettingPage(
        title = strings.cloudSettingsTitle,
        navigateBack = actions::navigateBack,
        rightAction = TopBarValidateAction(
            onClick = actions::saveChanges,
        ),
        contentPadding = PaddingValues(all = UiConstants.Spacing.large)
    ) {
        item {
            state.urlField.TextField(
                modifier = Modifier.fillMaxWidth(),
                focusManager = LocalFocusManager.current,
            )
        }
    }
}