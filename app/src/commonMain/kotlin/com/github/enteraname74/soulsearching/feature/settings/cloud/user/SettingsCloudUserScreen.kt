package com.github.enteraname74.soulsearching.feature.settings.cloud.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.user.UserType
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_data_table
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_key
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsCloudUserScreen(
    actions: SettingsCloudUserActions,
    state: SettingsCloudUserState,
) {
    SettingPage(
        navigateBack = actions::navigateBack,
        title = strings.cloudUserSettings,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            SoulMenuElement(
                title = strings.userStorageTitle,
                subTitle = strings.userStorageText,
                leadIcon = CoreRes.drawable.ic_data_table,
                onClick = actions::toUserData,
            )
        }
        if (state.user?.type == UserType.Admin) {
            item {
                SoulMenuElement(
                    title = strings.inscriptionCodeSettingsTitle,
                    subTitle = strings.inscriptionCodeSettingsText,
                    leadIcon = CoreRes.drawable.ic_key,
                    onClick = actions::toCode,
                )
            }
        }
        item {
            AnimatedVisibility(
                modifier = Modifier
                    .padding(
                        top = UiConstants.Spacing.medium,
                    ),
                visible = state.user != null,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                SoulFilledButton(
                    text = strings.disconnect,
                    onClick = actions::disconnect
                )
            }
        }
    }
}
