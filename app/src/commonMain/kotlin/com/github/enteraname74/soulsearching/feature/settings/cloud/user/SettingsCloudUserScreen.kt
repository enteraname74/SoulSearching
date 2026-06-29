package com.github.enteraname74.soulsearching.feature.settings.cloud.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_key
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
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
                    .verticalScroll(rememberScrollState())
                    .padding(
                        all = UiConstants.Spacing.large,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (state.user?.type == User.Type.Admin) {
                    SoulMenuElement(
                        title = strings.generateCodeTitle,
                        subTitle = strings.generateCodeText,
                        leadIcon = CoreRes.drawable.ic_key,
                        onClick = actions::toCode,
                    )
                }
                AnimatedVisibility(
                    modifier = Modifier
                        .padding(
                            top = UiConstants.Spacing.medium,
                        ),
                    visible = state.user != null,
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