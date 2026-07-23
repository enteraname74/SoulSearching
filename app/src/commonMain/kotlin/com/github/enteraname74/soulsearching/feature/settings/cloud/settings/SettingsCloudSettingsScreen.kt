package com.github.enteraname74.soulsearching.feature.settings.cloud.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
        contentPadding = PaddingValues(all = UiConstants.Spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            state.urlField.TextField(
                modifier = Modifier
                    .widthIn(max = UiConstants.Size.textFieldMaxWidth)
                    .fillMaxWidth(),
                focusManager = LocalFocusManager.current,
            )
        }
    }
}
