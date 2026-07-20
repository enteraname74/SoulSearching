package com.github.enteraname74.soulsearching.feature.settings.cloud.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_person_filled
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.list.ShapeListStyle
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsCloudUsersScreen(
    actions: SettingsCloudUsersActions,
    state: SettingsCloudUsersState,
) {
    state.dialog?.Dialog()

    SettingPage(
        navigateBack = actions::navigateBack,
        title = strings.cloudUsersTitle,
        verticalPadding = UiConstants.Spacing.small,
        contentPadding = PaddingValues(all = UiConstants.Spacing.large)
    ) {
        items(
            count = state.users.size,
            key = { state.users[it].id }
        ) { index ->
            val user = state.users[index]
            SimpleUserView(
                user = user,
                style = ShapeListStyle.fromList(
                    listSize = state.users.size,
                    index = index,
                )
            )
        }
    }
}

@Composable
private fun SimpleUserView(
    user: UiSimpleUser,
    style: ShapeListStyle,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(style.shape())
            .background(SoulSearchingColorTheme.colorScheme.secondary)
            .padding(all = UiConstants.Spacing.large),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SoulIcon(
            icon = CoreRes.drawable.ic_person_filled,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            size = UiConstants.ImageSize.medium,
        )
        Text(
            modifier = Modifier
                .weight(1f),
            text = user.name,
            style = UiConstants.Typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
        )
        user.onDelete?.let {
            SoulIconButton(
                icon = CoreRes.drawable.ic_delete_filled,
                size = UiConstants.ImageSize.medium,
                onClick = it,
                colors = SoulButtonDefaults.colors(
                    containerColor = Color.Transparent,
                )
            )
        }
    }
}