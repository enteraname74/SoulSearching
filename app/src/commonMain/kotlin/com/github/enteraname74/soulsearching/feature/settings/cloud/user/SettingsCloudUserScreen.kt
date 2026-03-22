package com.github.enteraname74.soulsearching.feature.settings.cloud.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun SettingsCloudUserScreen(
    actions: SettingsCloudUserActions,
    state: SettingsCloudUserState,
) {
    SoulScreen {
        Column {
            SoulTopBar(
                title = strings.cloudUserSettings,
                leftAction = TopBarNavigationAction(
                    onClick = actions::navigateBack,
                ),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        all = UiConstants.Spacing.large,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedVisibility(
                    visible = state.canDisconnect,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                )  {
                    SoulFilledButton(
                        text = strings.disconnect,
                        onClick = actions::disconnect
                    )
                }
            }
        }
    }
}