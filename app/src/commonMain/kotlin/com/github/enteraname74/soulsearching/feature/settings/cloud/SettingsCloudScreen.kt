package com.github.enteraname74.soulsearching.feature.settings.cloud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_chevron_right
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_groups_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_info_outlined
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_person_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_play_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_settings_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_sync_alt
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_warning_filled
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import org.jetbrains.compose.resources.DrawableResource
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulTexts

@Composable
fun SettingsCloudScreen(
    actions: SettingsCloudActions,
    state: SettingsCloudState,
) {
    SettingPage(
        navigateBack = actions::navigateBack,
        rightAction = object : TopBarActionSpec {
            override val icon: DrawableResource = CoreRes.drawable.ic_info_outlined
            override val onClick: () -> Unit = actions::toExplanations
        },
        title = CoreRes.string.cloud_title.toTextSpec(),
    ) {
        item {
            AlphaWarningCard()
        }
        item {
            SoulMenuElement(
                title = if (state.user == null) {
                    CoreRes.string.cloud_sign_in.toTextSpec()
                } else {
                    state.user.username.toTextSpec()
                },
                subTitle = state.user?.let { SoulTexts.userType(it.type) },
                onClick = actions::onUserClick,
                leadIcon = CoreRes.drawable.ic_person_filled,
                trailIcon = CoreRes.drawable.ic_chevron_right,
            )
        }

        item {
            SoulMenuElement(
                title = CoreRes.string.cloud_settings_title.toTextSpec(),
                subTitle = CoreRes.string.cloud_settings_text.toTextSpec(),
                onClick = actions::toSettings,
                leadIcon = CoreRes.drawable.ic_settings_filled,
                trailIcon = CoreRes.drawable.ic_warning_filled.takeIf { state.hasUrl == false },
            )
        }

        item {
            SoulMenuElement(
                enabled = state.connectedFeaturesEnabled,
                title = CoreRes.string.cloud_users_title.toTextSpec(),
                subTitle = CoreRes.string.cloud_users_text.toTextSpec(),
                onClick = actions::toUsers,
                leadIcon = CoreRes.drawable.ic_groups_filled,
            )
        }

        item {
            SoulMenuElement(
                enabled = state.connectedFeaturesEnabled,
                title = CoreRes.string.cloud_sync_title.toTextSpec(),
                subTitle = CoreRes.string.cloud_sync_text.toTextSpec(),
                onClick = actions::toSync,
                leadIcon = CoreRes.drawable.ic_sync_alt,
            )
        }

        item {
            SoulMenuElement(
                enabled = state.connectedFeaturesEnabled,
                title = CoreRes.string.cloud_shared_list_title.toTextSpec(),
                subTitle = CoreRes.string.cloud_shared_list_text.toTextSpec(),
                onClick = actions::toSharedList,
                leadIcon = CoreRes.drawable.ic_play_filled,
            )
        }
    }
}

@Composable
private fun AlphaWarningCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(UiConstants.Spacing.large),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .widthIn(
                    min = CardMinWidth,
                    max = CardMaxWidth
                ),
            colors = CardDefaults.cardColors(
                contentColor = SoulSearchingColorTheme.colorScheme.text,
                containerColor = SoulSearchingColorTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(UiConstants.Spacing.large),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SoulIcon(
                    icon = CoreRes.drawable.ic_warning_filled,
                    size = UiConstants.ImageSize.mediumPlus,
                    color = SoulSearchingColorTheme.colorScheme.text,
                )
                SoulText(
                    text = CoreRes.string.cloud_alpha_warning_text.toTextSpec(),
                    color = SoulSearchingColorTheme.colorScheme.text,
                    textAlign = TextAlign.Center,
                    style = UiConstants.Typography.body,
                )
            }
        }
    }
}

private val CardMinWidth: Dp = 0.dp
private val CardMaxWidth: Dp = 500.dp
