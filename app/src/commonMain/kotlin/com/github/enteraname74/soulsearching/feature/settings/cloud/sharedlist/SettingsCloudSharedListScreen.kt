package com.github.enteraname74.soulsearching.feature.settings.cloud.sharedlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.button.SoulChoiceButtonData
import com.github.enteraname74.soulsearching.coreui.button.SoulChoiceIconButton
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_groups_filled
import com.github.enteraname74.soulsearching.coreui.list.ShapeListStyle
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun SettingsCloudSharedListScreen(
    actions: SettingsCloudSharedListActions,
    state: SettingsCloudSharedListState,
) {
    state.dialog?.Dialog()

    SettingPage(
        title = strings.cloudSharedListTitle,
        navigateBack = actions::navigateBack,
        verticalPadding = UiConstants.Spacing.small,
    ) {
        item {
            SoulMenuElement(
                title = strings.cloudSharedListJoinTitle,
                subTitle = strings.cloudSharedlistJoinText,
                onClick = actions::toJoin,
                leadIcon = CoreRes.drawable.ic_groups_filled,
            )
        }
        items(
            count = state.previews.size,
            key = { state.previews[it].id }
        ) { index ->
            val preview = state.previews[index]
            PreviewCard(
                preview = preview,
                style = ShapeListStyle.fromList(
                    listSize = state.previews.size,
                    index = index,
                )
            )
        }
    }
}

@Composable
private fun PreviewCard(
    preview: UiListPreview,
    style: ShapeListStyle,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UiConstants.Spacing.large)
            .clip(style.shape())
            .background(SoulSearchingColorTheme.colorScheme.secondary)
            .padding(all = UiConstants.Spacing.large),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CardContent(
            modifier = Modifier
                .weight(1f),
            preview = preview,
        )
        SoulChoiceIconButton(
            buttonColors = SoulButtonColors(
                contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                containerColor = Color.Transparent,
            ),
            onClick = { it.invoke() },
            choices = buildList {
                add(
                    SoulChoiceButtonData(
                        icon = null,
                        data = preview.onJoin,
                        title = strings.joinSharedListButton,
                    )
                )
                preview.onDelete?.let {
                    add(
                        SoulChoiceButtonData(
                            icon = null,
                            data = it,
                            title = strings.delete,
                        )
                    )
                }
            }
        )
    }
}

@Composable
private fun CardContent(
    preview: UiListPreview,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
    ) {
        Text(
            text = preview.date,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            style = UiConstants.Typography.titleSmall,
        )
        Text(
            text = preview.allUsers,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            style = UiConstants.Typography.body,
        )
        Text(
            text = preview.connectedUsers,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            style = UiConstants.Typography.body.copy(fontWeight = FontWeight.Bold),
        )
    }
}
