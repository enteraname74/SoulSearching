package com.github.enteraname74.soulsearching.feature.settings.cloud.code

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.PlayerMinimisedHeight

@Composable
fun SettingsCloudCodeScreen(
    actions: SettingsCloudCodeActions,
    state: SettingsCloudCodeState,
) {
    SoulScreen {
        Column {
            SoulTopBar(
                title = strings.cloudSettingsTitle,
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
                    )
                    .padding(
                        bottom = PlayerMinimisedHeight.toDp(),
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedVisibility(
                    visible = state.code != null,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    // TODO CLOUD: Add copy to clipboard
                    Text(
                        text = state.code.orEmpty(),
                        style = UiConstants.Typography.titleSmall,
                        color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    )
                }
                SoulFilledButton(
                    text = strings.generateCodeTitle,
                    onClick = actions::generateCode,
                )
            }
        }
    }
}