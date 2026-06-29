package com.github.enteraname74.soulsearching.feature.settings.cloud.sync

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction

@Composable
fun SettingsCloudSyncScreen(
    actions: SettingsCloudSyncActions,
    state: SettingsCloudSyncState,
) {
    SoulScreen {
        Column {
            SoulTopBar(
                title = strings.cloudSyncTitle,
                leftAction = TopBarNavigationAction(
                    onClick = actions::navigateBack,
                ),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        all = UiConstants.Spacing.large,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SoulFilledButton(
                    text = strings.cloudSyncButton,
                    onClick = actions::launchSync,
                    enabled = !state.isSyncing,
                )
            }
        }
    }
}