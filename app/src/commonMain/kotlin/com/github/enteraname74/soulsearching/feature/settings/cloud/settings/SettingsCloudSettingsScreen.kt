package com.github.enteraname74.soulsearching.feature.settings.cloud.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction

@Composable
fun SettingsCloudSettingsScreen(
    actions: SettingsCloudSettingsActions,
    state: SettingsCloudSettingsState,
) {
    SoulScreen {
        Column {
            SoulTopBar(
                title = strings.cloudSettingsTitle,
                leftAction = TopBarNavigationAction(
                    onClick = actions::navigateBack,
                ),
                rightAction = TopBarValidateAction(
                    onClick = actions::saveChanges,
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        all = UiConstants.Spacing.large,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                state.urlField.TextField(
                    modifier = Modifier.fillMaxWidth(),
                    focusManager = LocalFocusManager.current,
                )
            }
        }
    }
}