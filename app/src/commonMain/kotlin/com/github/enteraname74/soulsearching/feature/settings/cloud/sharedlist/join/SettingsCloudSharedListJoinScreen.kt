package com.github.enteraname74.soulsearching.feature.settings.cloud.sharedlist.join

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.cloud_shared_list_title
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.join_shared_list_button
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.topbar.SoulAdaptiveTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarAction
import com.github.enteraname74.soulsearching.coreui.utils.PlayerMinimisedHeight

@Composable
fun SettingsCloudSharedListJoinScreen(
    actions: SettingsCloudSharedListJoinActions,
    state: SettingsCloudSharedListJoinState,
) {
    SoulScreen {
        val focusManager = LocalFocusManager.current

        Column {
            SoulAdaptiveTopBar(
                title = CoreRes.string.cloud_shared_list_title.toTextSpec(),
                leftAction = SoulTopBarAction.navigation(
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
                    modifier = Modifier
                        .widthIn(max = UiConstants.Size.textFieldMaxWidth)
                        .fillMaxWidth(),
                    focusManager = focusManager,
                )
                SoulFilledButton(
                    text = CoreRes.string.join_shared_list_button.toTextSpec(),
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
