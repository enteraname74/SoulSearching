package com.github.enteraname74.soulsearching.feature.settings.cloud.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.domain.model.user.UserType
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulFilledButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_data_table
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_key
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun SettingsCloudUserScreen(
    actions: SettingsCloudUserActions,
    state: SettingsCloudUserState,
) {
    SettingPage(
        navigateBack = actions::navigateBack,
        title = CoreRes.string.cloud_user_settings.toTextSpec(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            SoulMenuElement(
                title = CoreRes.string.user_storage_title.toTextSpec(),
                subTitle = CoreRes.string.user_storage_text.toTextSpec(),
                leadIcon = CoreRes.drawable.ic_data_table,
                onClick = actions::toUserData,
            )
        }
        if (state.user?.type == UserType.Admin) {
            item {
                SoulMenuElement(
                    title = CoreRes.string.inscription_code_settings_title.toTextSpec(),
                    subTitle = CoreRes.string.inscription_code_settings_text.toTextSpec(),
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
                    text = CoreRes.string.disconnect.toTextSpec(),
                    onClick = actions::disconnect
                )
            }
        }
    }
}
