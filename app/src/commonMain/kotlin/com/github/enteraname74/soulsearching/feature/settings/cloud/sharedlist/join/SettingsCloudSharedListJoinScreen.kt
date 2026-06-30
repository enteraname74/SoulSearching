package com.github.enteraname74.soulsearching.feature.settings.cloud.sharedlist.join

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.utils.PlayerMinimisedHeight

@Composable
fun SettingsCloudSharedListJoinScreen(
    actions: SettingsCloudSharedListJoinActions,
    state: SettingsCloudSharedListJoinState,
) {
    SoulScreen {
        val focusManager = LocalFocusManager.current

        Column {
            SoulTopBar(
                title = strings.cloudSharedListTitle,
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
                        bottom = PlayerMinimisedHeight.toDp()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.mediumPlus)
            ) {
                state.codeField.TextField(
                    modifier = Modifier.fillMaxWidth(),
                    focusManager = focusManager,
                )
                SoulFilledButton(
                    text = strings.joinSharedListButton,
                    enabled = state.codeField.value.isNotBlank(),
                    onClick = {
                        focusManager.clearFocus()
                        actions.join()
                    },
                )
            }
        }
    }
}